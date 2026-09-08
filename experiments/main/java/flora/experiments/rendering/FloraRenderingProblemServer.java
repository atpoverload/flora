package flora.experiments.rendering;

import static flora.util.LoggerUtil.getLogger;

import flora.MeteringMachine;
import flora.contrib.ears.FloraProblem;
import flora.knob.RangeKnob;

import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;

import org.um.feri.ears.algorithms.MOAlgorithm;
import org.um.feri.ears.algorithms.StateManager;
import org.um.feri.ears.algorithms.moo.ibea.D_IBEA;
import org.um.feri.ears.algorithms.moo.moead.D_MOEAD;
import org.um.feri.ears.algorithms.moo.nsga2.D_NSGAII;
import org.um.feri.ears.problems.NumberProblem;
import org.um.feri.ears.problems.NumberSolution;
import org.um.feri.ears.problems.StopCriterion;
import org.um.feri.ears.problems.Task;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

public class FloraRenderingProblemServer {
    private static final Logger logger = getLogger();

    private static final Integer PORT = Integer.valueOf(8980);
    private static final Path STATE_FILE_PATH = Path.of("/tmp", "state.json");
    private static final Path RESULT_FILE_PATH = Path.of("/tmp", "result.json");
    private static final RenderingKnobs DEFAULT_KNOBS =
            RenderingKnobs.newBuilder()
                    .setResolutionX(RangeKnob.newBuilder().setStart(100).setEnd(1000).setStep(50))
                    .setResolutionY(RangeKnob.newBuilder().setStart(100).setEnd(1000).setStep(50))
                    .setAaSamples(RangeKnob.newBuilder().setStart(-2).setEnd(2).setStep(1))
                    .setAoSamples(RangeKnob.newBuilder().setStart(0).setEnd(96).setStep(1))
                    .addAllFilter(List.of("BOX", "GAUSSIAN", "BLACKMAN_HARRIS"))
                    .build();

    private static final MeteringMachine createMeters(FloraRenderingProblemServerImpl serverImpl) {
        return new MeteringMachine(
                Map.of(
                        "energy",
                        new RenderingScoreMachine.RenderingScoreMeter(
                                () -> serverImpl.currentScore.get().get().getEnergy()),
                        "runtime",
                        new RenderingScoreMachine.RenderingScoreMeter(
                                () -> serverImpl.currentScore.get().get().getRuntime()),
                        "piqe",
                        new RenderingScoreMachine.RenderingScoreMeter(
                                () -> serverImpl.currentScore.get().get().getPiqe()),
                        "mse",
                        new RenderingScoreMachine.RenderingScoreMeter(
                                () -> serverImpl.currentScore.get().get().getMse())));
    }

    private enum ModelKind {
        NSGA,
        MOEAD,
        IBEA;
    }

    private static final MOAlgorithm<Double, NumberSolution<Double>, NumberProblem<Double>>
            createModel(ModelKind model) {
        return switch (model) {
            case NSGA -> new D_NSGAII();
            case MOEAD -> new D_MOEAD();
            case IBEA -> new D_IBEA();
        };
    }

    /** Spins up the server. */
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            logger.info(String.format("starting new flora server at localhost:%d", PORT));
            System.exit(1);
        }
        ModelKind modelKind = ModelKind.valueOf(args[0]);

        logger.info(String.format("starting new flora server at localhost:%d", PORT));

        FloraRenderingProblemServerImpl serverImpl = new FloraRenderingProblemServerImpl();
        final Server server =
                Grpc.newServerBuilderForPort(PORT, InsecureServerCredentials.create())
                        .addService(serverImpl)
                        .build();
        server.start();
        final AtomicReference<StateManager> model = new AtomicReference<>();
        final AtomicReference<
                        FloraProblem<RenderingKnobs, RenderingConfiguration, RenderingWorkUnit>>
                results = new AtomicReference<>();
        Runtime.getRuntime()
                .addShutdownHook(
                        new Thread("flora-rendering-problem-server-shutdown") {
                            @Override
                            public void run() {
                                try {
                                    System.out.println(
                                            "shutting down flora server since the JVM is shutting"
                                                    + " down");
                                    if (server != null) {
                                        server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
                                    }
                                    model.get().saveState(STATE_FILE_PATH.toString());
                                    System.out.println("writing result to" + RESULT_FILE_PATH);
                                    JsonSceneUtil.writeResults(results.get(), RESULT_FILE_PATH);
                                } catch (Exception e) {
                                    e.printStackTrace(System.err);
                                }
                                System.out.println("server shutdown...");
                            }
                        });
        Files.deleteIfExists(STATE_FILE_PATH);
        try {
            while (true) {
                var model1 = createModel(modelKind);
                model.set((StateManager) model1);
                if (Files.exists(STATE_FILE_PATH)) {
                    model.get().loadState(STATE_FILE_PATH.toString(), false);
                }
                final FloraProblem<RenderingKnobs, RenderingConfiguration, RenderingWorkUnit>
                        problem =
                                new FloraProblem<>(
                                        "flora-rendering-problem-server",
                                        new RenderingWorkFactory(
                                                DEFAULT_KNOBS,
                                                serverImpl.nextConfiguration,
                                                serverImpl::fetchLastScore),
                                        createMeters(serverImpl));
                results.set(problem);
                model1.execute(new Task<>(problem, StopCriterion.EVALUATIONS, 500, 0, 0));
                logger.info(String.format("writing result to %s", RESULT_FILE_PATH));
                JsonSceneUtil.writeResults(problem, RESULT_FILE_PATH);
                model.get().saveState(STATE_FILE_PATH.toString());
                break;
            }
        } catch (Exception e) {
            logger.warning(String.format("something failed: %s", e));
            e.printStackTrace();
        }
        logger.info(String.format("terminating flora server at localhost:%d", PORT));
    }
}
