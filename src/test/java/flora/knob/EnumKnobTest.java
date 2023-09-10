package flora.knob;

import static org.junit.Assert.assertEquals;

import flora.Knob;
import org.junit.Test;

public class EnumKnobTest {
  private enum A {
    X,
    Y,
  }

  private static final int GOOD_INDEX = 0;
  private static final int BAD_INDEX = A.values().length;

  @Test
  public void configurationCount_success() {
    Knob knob = new EnumKnob<>(A.class);

    assertEquals(A.values().length, knob.configurationCount());
  }

  @Test
  public void fromIndex_success() {
    EnumKnob<A> knob = new EnumKnob<>(A.class);

    assertEquals(A.X, knob.fromIndex(0));
    assertEquals(A.Y, knob.fromIndex(1));
  }

  @Test
  public void fromIndex_generic_success() {
    Knob knob = new EnumKnob<>(A.class);

    assertEquals(A.X, knob.fromIndex(0, A.class));
    assertEquals(A.Y, knob.fromIndex(1, A.class));
  }

  // TODO: parameterize these tests
  @Test
  public void fromIndex_indexFailure() {
    EnumKnob<A> knob = new EnumKnob<>(A.class);
    KnobValueException expectedError = new KnobValueException(knob, A.class, BAD_INDEX);

    try {
      knob.fromIndex(BAD_INDEX);
      throw new IllegalStateException(
          String.format("%s should not have a value for %d", knob, BAD_INDEX));
    } catch (KnobValueException e) {
      assertEquals(expectedError.getMessage(), e.getMessage());
    } catch (Exception e) {
      throw new IllegalStateException(
          String.format(
              "%s should have failed for %d with %s but got %s",
              knob, BAD_INDEX, expectedError, e));
    }
  }

  @Test
  public void fromIndex_generic_indexFailure() {
    Knob knob = new EnumKnob<>(A.class);
    KnobValueException expectedError = new KnobValueException(knob, A.class, BAD_INDEX);

    try {
      knob.fromIndex(BAD_INDEX, A.class);
      throw new IllegalStateException(
          String.format("%s should not have a value for %d", knob, BAD_INDEX));
    } catch (KnobValueException e) {
      assertEquals(expectedError.getMessage(), e.getMessage());
    } catch (Exception e) {
      throw new IllegalStateException(
          String.format(
              "%s should have failed for %d with %s but got %s",
              knob, BAD_INDEX, expectedError, e));
    }
  }

  @Test
  public void fromIndex_generic_typeFailure() {
    Knob knob = new EnumKnob<>(A.class);
    KnobValueException expectedError = new KnobValueException(knob, String.class, GOOD_INDEX);

    try {
      knob.fromIndex(GOOD_INDEX, String.class);
      throw new IllegalStateException(
          String.format("%s should not have a value for %d", knob, GOOD_INDEX));
    } catch (KnobValueException e) {
      assertEquals(expectedError.getMessage(), e.getMessage());
    } catch (Exception e) {
      throw new IllegalStateException(
          String.format(
              "%s should have failed for %d with %s but got %s",
              knob, GOOD_INDEX, expectedError, e));
    }
  }
}
