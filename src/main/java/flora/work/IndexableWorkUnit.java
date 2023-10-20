package flora.work;

import flora.WorkUnit;

public interface IndexableWorkUnit<K, C, W extends WorkUnit<K, C>> extends WorkUnit<K, C> {
  W fromIndices(int[] indices);
}
