package flora.experiments.rendering;

import static flora.util.LoggerUtil.getLogger;

import flora.contrib.ears.FloraProblem;
import flora.knob.RangeKnob;
import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import org.um.feri.ears.algorithms.moo.nsga2.D_NSGAII;
import org.um.feri.ears.problems.StopCriterion;
import org.um.feri.ears.problems.Task;

public class FloraRenderingProblemServer {
  private static final Logger logger = getLogger();

  private static final Integer DEFAULT_PORT = Integer.valueOf(8980);

  /** Spins up the server. */
  public static void main(String[] args) throws Exception {
    logger.info(String.format("starting new flora server at localhost:%d", DEFAULT_PORT));

    FloraRenderingProblemServerImpl serverImpl = new FloraRenderingProblemServerImpl();
    final Server server =
        Grpc.newServerBuilderForPort(DEFAULT_PORT, InsecureServerCredentials.create())
            .addService(serverImpl)
            .build();
    server.start();
    Runtime.getRuntime()
        .addShutdownHook(
            new Thread("flora-rendering-problem-server-shutdown") {
              @Override
              public void run() {
                // TODO: i locked up this logger to here. is that good enough?
                logger.info("shutting down flora server since the JVM is shutting down");
                try {
                  if (server != null) {
                    server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
                  }
                } catch (InterruptedException e) {
                  e.printStackTrace(System.err);
                }
                logger.info("server shutdown...");
              }
            });
    D_NSGAII nsga = new D_NSGAII();
    nsga.execute(
        new Task<>(
            new FloraProblem<RenderingKnobs, RenderingConfiguration, RenderingWorkUnit>(
                "flora-rendering-problem-server",
                new RenderingWorkFactory(
                    RenderingKnobs.newBuilder()
                        .setResolutionX(RangeKnob.newBuilder().setStart(1).setEnd(10))
                        .setResolutionY(RangeKnob.newBuilder().setStart(1).setEnd(10))
                        .build(),
                    serverImpl.nextConfiguration),
                new RenderingScoreMachine(serverImpl.lastScore)),
            StopCriterion.EVALUATIONS,
            10000,
            0,
            0));
    logger.info(String.format("terminating flora server at localhost:%d", DEFAULT_PORT));
  }
}
