package flora.strategy.contrib.ears;

import flora.WorkloadContext;

public record EarsContext(EarsKnob[] knobs, int[] configuration)
    implements WorkloadContext<EarsKnob[], int[]> {}
