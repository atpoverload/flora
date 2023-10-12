package flora.context;

import flora.knob.BooleanKnob;
import java.util.concurrent.ThreadLocalRandom;

public final class BooleanContext
    implements RandomizableContext<BooleanKnob, Boolean, BooleanContext> {
  public static final BooleanContext TRUE_CONTEXT = new BooleanContext(true);
  public static final BooleanContext FALSE_CONTEXT = new BooleanContext(false);

  /** Returns one of the two possible contexts. */
  public static BooleanContext randomContext() {
    return ThreadLocalRandom.current().nextBoolean() ? TRUE_CONTEXT : FALSE_CONTEXT;
  }

  private final boolean value;

  private BooleanContext(boolean value) {
    this.value = value;
  }

  @Override
  public BooleanKnob knobs() {
    return BooleanKnob.INSTANCE;
  }

  @Override
  public Boolean configuration() {
    return value;
  }

  @Override
  public BooleanContext random() {
    return randomContext();
  }
}
