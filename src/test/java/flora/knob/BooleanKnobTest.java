package flora.knob;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import flora.Knob;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class BooleanKnobTest {
  private static final int GOOD_INDEX = 0;
  private static final int BAD_INDEX = 2;

  @Test
  public void configurationCount_success() {
    Knob knob = BooleanKnob.instance();

    assertEquals(2, knob.configurationCount());
  }

  @Test
  public void fromIndex_success() {
    BooleanKnob knob = BooleanKnob.instance();

    assertFalse(knob.fromIndex(0));
    assertTrue(knob.fromIndex(1));
  }

  @Test
  public void fromIndex_generic_success() {
    Knob knob = BooleanKnob.instance();

    assertFalse(knob.fromIndex(0, Boolean.class));
    assertTrue(knob.fromIndex(1, Boolean.class));
  }

  // TODO: parameterize these tests
  @Test
  public void fromIndex_badIndex_valueException() {
    BooleanKnob knob = BooleanKnob.instance();
    KnobValueException expectedError = new KnobValueException(knob, Boolean.class, BAD_INDEX);

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
  public void fromIndex_generic_badType_valueException() {
    Knob knob = BooleanKnob.instance();
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

  @Test
  public void fromIndex_generic_badIndex_valueException() {
    Knob knob = BooleanKnob.instance();
    KnobValueException expectedError = new KnobValueException(knob, Boolean.class, BAD_INDEX);

    try {
      knob.fromIndex(BAD_INDEX, Boolean.class);
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
}
