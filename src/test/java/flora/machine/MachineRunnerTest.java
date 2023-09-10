package flora.machine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import flora.Machine;
import flora.Meter;
import flora.Strategy;
import flora.testing.ConstantContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public class MachineRunnerTest {
  private enum MachineStage {
    CONTEXT,
    METER,
    METER_START,
    RUN,
    METER_STOP,
    METER_READ,
    UPDATE,
  }

  /** This smoke test ensures that the {@link MachineRunner} works with minimal expectations. */
  @Test
  public void smokeTest_pass() {
    final ArrayList<MachineStage> stages = new ArrayList<>();
    Machine<Boolean, Boolean, ConstantContext<Boolean>> machine =
        new Machine<>() {
          @Override
          public void runWorkload(ConstantContext<Boolean> context) {
            assertTrue(context.knobs());
            assertTrue(context.configuration());
            stages.add(MachineStage.RUN);
          }

          @Override
          public Map<String, Meter> meters() {
            stages.add(MachineStage.METER);
            return Map.of(
                "meter",
                new Meter() {
                  @Override
                  public void start() {
                    stages.add(MachineStage.METER_START);
                  }

                  @Override
                  public void stop() {
                    stages.add(MachineStage.METER_STOP);
                  }

                  @Override
                  public double read() {
                    stages.add(MachineStage.METER_READ);
                    return 1;
                  }
                });
          }

          @Override
          public Strategy<Boolean, Boolean, ConstantContext<Boolean>> strategy() {
            return new Strategy<Boolean, Boolean, ConstantContext<Boolean>>() {
              @Override
              public ConstantContext<Boolean> context() {
                stages.add(MachineStage.CONTEXT);
                return new ConstantContext<>(true);
              }

              @Override
              public void update(
                  ConstantContext<Boolean> context, Map<String, Double> measurement) {
                assertTrue(context.knobs());
                assertTrue(context.configuration());
                assertTrue(1 == measurement.size());
                assertTrue(1 == measurement.get("meter"));
                stages.add(MachineStage.UPDATE);
              }
            };
          }
        };

    MachineRunner.run(machine);

    assertEqualContents(
        List.of(
            MachineStage.CONTEXT,
            MachineStage.METER,
            MachineStage.METER_START,
            MachineStage.RUN,
            MachineStage.METER_STOP,
            MachineStage.METER_READ,
            MachineStage.UPDATE),
        stages);
  }

  /** This smoke test ensures that the {@link MachineRunner} works with minimal expectations. */
  @Test
  public void smokeTest_fail() {
    final ArrayList<MachineStage> stages = new ArrayList<>();
    Machine<Boolean, Boolean, ConstantContext<Boolean>> machine =
        new Machine<>() {
          @Override
          public void runWorkload(ConstantContext<Boolean> context) {
            stages.add(MachineStage.RUN);
            throw new TestError();
          }

          @Override
          public Map<String, Meter> meters() {
            stages.add(MachineStage.METER);
            return Map.of(
                "meter",
                new Meter() {
                  @Override
                  public void start() {
                    stages.add(MachineStage.METER_START);
                  }

                  @Override
                  public void stop() {
                    stages.add(MachineStage.METER_STOP);
                  }

                  @Override
                  public double read() {
                    stages.add(MachineStage.METER_READ);
                    return 1;
                  }
                });
          }

          @Override
          public Strategy<Boolean, Boolean, ConstantContext<Boolean>> strategy() {
            return new Strategy<Boolean, Boolean, ConstantContext<Boolean>>() {
              @Override
              public ConstantContext<Boolean> context() {
                stages.add(MachineStage.CONTEXT);
                return new ConstantContext<>(true);
              }

              @Override
              public void update(
                  ConstantContext<Boolean> context, Map<String, Double> measurement) {
                assertTrue(context.knobs());
                assertTrue(context.configuration());
                assertTrue(1 == measurement.size());
                assertTrue(1 == measurement.get("meter"));
                stages.add(MachineStage.UPDATE);
              }
            };
          }
        };

    TestError expectedError = new TestError();
    try {
      MachineRunner.run(machine);
      throw new IllegalStateException(
          String.format("%s should have failed with a throwing worklaod", machine));
    } catch (IllegalArgumentException e) {
      assertEquals(
          "The workload failed with the given configuration, so the meters were stopped.",
          e.getMessage());
      assertEquals(expectedError.getMessage(), e.getCause().getMessage());
    } catch (Exception e) {
      throw new IllegalStateException(
          String.format("%s should have failed with %s but got %s", machine, expectedError, e));
    }

    assertEqualContents(
        List.of(
            MachineStage.CONTEXT,
            MachineStage.METER,
            MachineStage.METER_START,
            MachineStage.RUN,
            MachineStage.METER_STOP,
            MachineStage.METER_READ),
        stages);
  }

  private void assertEqualContents(List<MachineStage> expected, List<MachineStage> actual) {
    assertEquals(expected.size(), actual.size());
    for (int i = 0; i < expected.size(); i++) {
      assertEquals(expected.get(i), actual.get(i));
    }
  }

  private static class TestError extends RuntimeException {
    private TestError() {
      super("i failed!");
    }
  }
}
