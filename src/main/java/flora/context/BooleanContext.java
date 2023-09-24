package flora.context;

import flora.knob.BooleanKnob;
import java.util.concurrent.ThreadLocalRandom;

public final class BooleanContext
    implements RandomizableContext<BooleanKnob, Boolean, BooleanContext> {
  public static final BooleanContext TRUE_CONTEXT = new BooleanContext(true);
  public static final BooleanContext FALSE_CONTEXT = new BooleanContext(false);

  private final boolean value;

  private BooleanContext(boolean value) {
    this.value = value;
  }

  @Override
  public BooleanKnob knobs() {
    return BooleanKnob.instance();
  }

  @Override
  public Boolean configuration() {
    return value;
  }

  @Override
  public BooleanContext random() {
    return ThreadLocalRandom.current().nextBoolean() ? TRUE_CONTEXT : FALSE_CONTEXT;
  }
}
