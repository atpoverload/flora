package green.knob;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

import green.Knob;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/** Helper for common {@link Knob} use cases. */
public final class Knobs {
  /** Create a {@link Knob} that has a random value within the constraints of another knob. */
  // TODO: there might be a debt burden here
  public static Knob withRandomValue(Knob k) {
    if (k instanceof BooleanKnob) {
      return new BooleanKnob(ThreadLocalRandom.current().nextBoolean());
    } else if (k instanceof IntRangeKnob) {
      IntRangeKnob knob = new IntRangeKnob((IntRangeKnob) k);
      int range = (knob.getEnd() - knob.getStart() + 1) / knob.getStep();
      knob.setValue(
          knob.getStep() * ThreadLocalRandom.current().nextInt(0, range) + knob.getStart());
      return knob;
    } else if (k instanceof IntCollectionKnob) {
      IntCollectionKnob knob = new IntCollectionKnob((IntCollectionKnob) k);
      int[] values = knob.getValues();
      knob.setValue(values[ThreadLocalRandom.current().nextInt(values.length)]);
      return knob;
    } else if (k instanceof EnumKnob) {
      EnumKnob<?> knob = new EnumKnob<>((EnumKnob<?>) k);
      String[] names = knob.getNames();
      knob.setValue(names[ThreadLocalRandom.current().nextInt(names.length)]);
      return knob;
    } else {
      throw new IllegalArgumentException(
          String.format(
              "ThreadLocalRandom.current()ization of %s is not supported.",
              k.getClass().getSimpleName()));
    }
  }

  /** Create a copy of {@link Knobs} with ThreadLocalRandom.current() values. */
  public static Map<String, Knob> withRandomValues(Map<String, Knob> knobs) {
    return knobs.entrySet().stream()
        .collect(toMap(e -> e.getKey(), e -> withRandomValue(e.getValue())));
  }

  /** Gets the total configuration count of a collection of knobs. */
  public static int getConfigurationCount(Collection<Knob> knobs) {
    int count = 1;
    for (Knob k : knobs) {
      if (k instanceof BooleanKnob) {
        count *= 2;
      } else if (k instanceof IntCollectionKnob) {
        count *= ((IntCollectionKnob) k).getValues().length;
      } else if (k instanceof IntRangeKnob) {
        IntRangeKnob knob = (IntRangeKnob) k;
        count *= (knob.getEnd() - knob.getStart()) / knob.getStep();
      } else if (k instanceof EnumKnob) {
        count *= ((EnumKnob<?>) k).getNames().length;
      } else {
        throw new IllegalArgumentException(
            String.format(
                "Configuration counting of %s is not supported.", k.getClass().getSimpleName()));
      }
    }
    return count;
  }

  /** Gets the total configuration count of a mapping of knobs. */
  public static int getConfigurationCount(Map<String, Knob> knobs) {
    return getConfigurationCount(knobs.values());
  }

  /** Writes the knob as a json dict. */
  public static String toJson(Knob k) {
    if (k instanceof BooleanKnob) {
      return Boolean.toString(k.getBoolean());
    } else if (k instanceof IntRangeKnob || k instanceof IntCollectionKnob) {
      return Integer.toString(k.getInt());
    } else if (k instanceof EnumKnob) {
      return ((EnumKnob<?>) k).getNames()[k.getInt()];
    } else {
      throw new IllegalArgumentException(
          String.format("%s's value can't be turned into a string.", k.getClass().getSimpleName()));
    }
  }

  /** Writes the knob values as a json dict. */
  public static String valuesToJson(Map<String, Knob> knobs) {
    return String.format(
        "{%s}",
        knobs.entrySet().stream()
            .map(e -> String.format("\"%s\":%s", e.getKey(), valueToJson(e.getValue())))
            .collect(joining(",")));
  }

  /** Extract the value from a {@link Knob} as a json value. */
  private static String valueToJson(Knob k) {
    if (k instanceof BooleanKnob) {
      return Boolean.toString(k.getBoolean());
    } else if (k instanceof IntRangeKnob || k instanceof IntCollectionKnob) {
      return Integer.toString(k.getInt());
    } else if (k instanceof EnumKnob) {
      return String.format("\"%s\"", ((EnumKnob<?>) k).getNames()[k.getInt()]);
    } else {
      throw new IllegalArgumentException(
          String.format("%s's value can't be turned into a string.", k.getClass().getSimpleName()));
    }
  }

  private Knobs() {}
}
