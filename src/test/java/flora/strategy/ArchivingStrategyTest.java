package flora.strategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import flora.strategy.ArchivingStrategy.ArchiveRecordSummary;
import flora.testing.ConstantContext;
import java.util.Map;
import org.junit.Test;

// TODO: might want golden tests for some of these
public class ArchivingStrategyTest {
  @Test
  public void update() {
    ArchivingStrategy<Boolean, Boolean, ConstantContext<Boolean>> strategy =
        new ArchivingStrategy<>() {
          @Override
          public ConstantContext<Boolean> context() {
            return new ConstantContext<Boolean>(true);
          }
        };

    strategy.update(strategy.context(), Map.of("meter", 1.0));

    assertTrue(1 == strategy.data().size());
    assertTrue(strategy.data().get(0).context().knobs());
    assertTrue(strategy.data().get(0).context().configuration());
    assertEquals(1.0, strategy.data().get(0).measurement().get("meter"), 0.0);
  }

  @Test
  public void update_multiple() {
    ArchivingStrategy<Boolean, Boolean, ConstantContext<Boolean>> strategy =
        new ArchivingStrategy<>() {
          @Override
          public ConstantContext<Boolean> context() {
            return new ConstantContext<Boolean>(true);
          }
        };

    strategy.update(strategy.context(), Map.of("meter", 1.0));
    strategy.update(new ConstantContext<Boolean>(false), Map.of("meter", 2.0));

    assertTrue(2 == strategy.data().size());

    assertTrue(strategy.data().get(0).context().knobs());
    assertTrue(strategy.data().get(0).context().configuration());
    assertEquals(1.0, strategy.data().get(0).measurement().get("meter"), 0.0);

    assertFalse(strategy.data().get(1).context().knobs());
    assertFalse(strategy.data().get(1).context().configuration());
    assertEquals(2.0, strategy.data().get(1).measurement().get("meter"), 0.0);
  }

  @Test
  public void summary() {
    ArchivingStrategy<Boolean, Boolean, ConstantContext<Boolean>> strategy =
        new ArchivingStrategy<>() {
          @Override
          public ConstantContext<Boolean> context() {
            return new ConstantContext<Boolean>(true);
          }
        };

    strategy.update(strategy.context(), Map.of("meter", 1.0));
    strategy.update(strategy.context(), Map.of("meter", 2.0));
    strategy.update(strategy.context(), Map.of("meter", 3.0));

    strategy.update(new ConstantContext<Boolean>(false), Map.of("meter2", 2.0));
    strategy.update(new ConstantContext<Boolean>(false), Map.of("meter2", 4.0));
    strategy.update(new ConstantContext<Boolean>(false), Map.of("meter2", 6.0));
    strategy.update(new ConstantContext<Boolean>(false), Map.of("meter2", 8.0));

    ArchiveRecordSummary<Boolean, Boolean, ConstantContext<Boolean>> summary = strategy.summary();

    assertTrue(2 == summary.counts().size());

    assertTrue(3 == summary.counts().get(true).get("meter"));
    assertEquals(2.0, summary.means().get(true).get("meter"), 0.0);
    assertEquals(0.817, summary.deviations().get(true).get("meter"), 0.01);

    assertTrue(4 == summary.counts().get(false).get("meter2"));
    assertEquals(5.0, summary.means().get(false).get("meter2"), 0.0);
    assertEquals(2.23, summary.deviations().get(false).get("meter2"), 0.01);
  }
}
