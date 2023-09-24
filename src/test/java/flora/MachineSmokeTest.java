package flora;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public class MachineSmokeTest {
  private static final SmokeTestContext CONTEXT = new SmokeTestContext();

  private enum MachineStage {
    METER,
    METER_START,
    RUN,
    METER_STOP,
    METER_READ,
  }

  /** This smoke test ensures that the {@link MachineRunner} works with minimal expectations. */
  @Test
  public void smokeTest_pass() {
    SmokeTestMachine machine =
        new SmokeTestMachine() {
          @Override
          public void runWorkload(SmokeTestContext context) {
            assertEquals(context.knobs(), null);
            assertEquals(context.configuration(), null);
            this.stages.add(MachineStage.RUN);
          }
        };

    machine.run(CONTEXT);

    assertEqualContents(
        List.of(
            MachineStage.METER,
            MachineStage.METER_START,
            MachineStage.RUN,
            MachineStage.METER_STOP,
            MachineStage.METER_READ),
        machine.stages);
  }

  /** This smoke test ensures that the {@link MachineRunner} works with minimal expectations. */
  @Test
  public void smokeTest_fail() {
    SmokeTestMachine machine =
        new SmokeTestMachine() {
          @Override
          public void runWorkload(SmokeTestContext context) {
            stages.add(MachineStage.RUN);
            throw new TestError();
          }
        };

    TestError expectedError = new TestError();
    try {
      machine.run(CONTEXT);
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
            MachineStage.METER,
            MachineStage.METER_START,
            MachineStage.RUN,
            MachineStage.METER_STOP,
            MachineStage.METER_READ),
        machine.stages);
  }

  private void assertEqualContents(List<MachineStage> expected, List<MachineStage> actual) {
    assertEquals(expected.size(), actual.size());
    for (int i = 0; i < expected.size(); i++) {
      assertEquals(expected.get(i), actual.get(i));
    }
  }

  private abstract static class SmokeTestMachine extends Machine<Object, Object, SmokeTestContext> {
    ArrayList<MachineStage> stages = new ArrayList<>();

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
  }

  private static class SmokeTestContext implements WorkloadContext<Object, Object> {
    @Override
    public Object knobs() {
      return null;
    }

    @Override
    public Object configuration() {
      return null;
    }
  }

  private static class TestError extends RuntimeException {
    private TestError() {
      super("i failed!");
    }
  }
}
