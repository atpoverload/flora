package flora.strategy.archiving;

import static flora.context.BooleanContext.FALSE_CONTEXT;
import static flora.context.BooleanContext.TRUE_CONTEXT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import flora.context.BooleanContext;
import flora.knob.BooleanKnob;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

// TODO: might want golden tests for some of these
@RunWith(JUnit4.class)
public class ArchivingStrategyTest {
  @Test
  public void update() {
    TestStrategy strategy = new TestStrategy(TRUE_CONTEXT);

    strategy.update(strategy.context(), Map.of("meter", 1.0));

    assertTrue(1 == strategy.records().size());
    assertTrue(strategy.records().get(0).context().configuration());
    assertEquals(1.0, strategy.records().get(0).measurement().get("meter"), 0.0);
  }

  @Test
  public void update_multiple() {
    TestStrategy strategy = new TestStrategy(TRUE_CONTEXT);

    strategy.update(strategy.context(), Map.of("meter", 1.0));

    strategy.setContext(FALSE_CONTEXT);

    strategy.update(strategy.context(), Map.of("meter", 2.0));

    assertTrue(2 == strategy.records().size());

    assertTrue(strategy.records().get(0).context().configuration());
    assertEquals(1.0, strategy.records().get(0).measurement().get("meter"), 0.0);

    assertFalse(strategy.records().get(1).context().configuration());
    assertEquals(2.0, strategy.records().get(1).measurement().get("meter"), 0.0);
  }

  @Test
  public void summary() {
    TestStrategy strategy = new TestStrategy(TRUE_CONTEXT);

    strategy.update(strategy.context(), Map.of("meter", 1.0));
    strategy.update(strategy.context(), Map.of("meter", 2.0));
    strategy.update(strategy.context(), Map.of("meter", 3.0));

    strategy.setContext(FALSE_CONTEXT);

    strategy.update(strategy.context(), Map.of("meter2", 2.0));
    strategy.update(strategy.context(), Map.of("meter2", 4.0));
    strategy.update(strategy.context(), Map.of("meter2", 6.0));
    strategy.update(strategy.context(), Map.of("meter2", 8.0));

    ArchiveRecordSummary<Boolean> summary = strategy.summary();

    assertTrue(2 == summary.counts().size());

    assertTrue(3 == summary.counts().get(true));
    assertEquals(2.0, summary.means().get(true).get("meter"), 0.0);
    assertEquals(0.817, summary.deviations().get(true).get("meter"), 0.01);

    assertTrue(4 == summary.counts().get(false));
    assertEquals(5.0, summary.means().get(false).get("meter2"), 0.0);
    assertEquals(2.23, summary.deviations().get(false).get("meter2"), 0.01);
  }

  private static class TestStrategy
      extends ArchivingStrategy<BooleanKnob, Boolean, BooleanContext> {
    private BooleanContext context;

    public TestStrategy(BooleanContext context) {
      setContext(context);
    }

    @Override
    public BooleanContext context() {
      return context;
    }

    public void setContext(BooleanContext context) {
      this.context = context;
    }
  }
  ;
}
