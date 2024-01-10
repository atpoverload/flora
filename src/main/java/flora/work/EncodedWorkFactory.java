package flora.work;

import flora.WorkUnit;

public interface EncodedWorkFactory<K, C, W extends WorkUnit<K, C>>
    extends RandomizableWorkFactory<K, C, W>, RepairingWorkFactory<K, C, W> {}
