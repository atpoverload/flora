package flora.context;

import flora.WorkloadContext;

public interface RandomizableContext<K, C, Ctx extends RandomizableContext<K, C, Ctx>> extends WorkloadContext<K, C> {
    Ctx random();
}
