DATA_DIR="data/cross-study"
mkdir "${DATA_DIR}"

MAX_HEAP_SIZE_FLAG="Xmx12G"

# LIB_CPUSCALER=bazel-bin/eflect/src/main/native
# java "-${MAX_HEAP_SIZE_FLAG}" -Djava.library.path="${LIB_CPUSCALER}" \
java "-${MAX_HEAP_SIZE_FLAG}" \
    -jar bazel-bin/experiments/main/java/flora/experiments/sunflow/renderers/SceneRenderer_deploy.jar \
    --knobs "${PWD}/experiments/main/resources/knob/default_knobs.json" \
    --configuration "${PWD}/experiments/main/resources/configuration/default_reference_configuration.json" \
    --scene "${PWD}/experiments/main/resources/scene/aliens_shiny.sc" \
    --output "${DATA_DIR}/aliens_shiny.json" \
    ${PWD}/experiments/main/resources/pareto_fronts/gumbo_and_teapot.json

# java "-${MAX_HEAP_SIZE_FLAG}" -Djava.library.path="${LIB_CPUSCALER}" \
java "-${MAX_HEAP_SIZE_FLAG}" \
    -jar bazel-bin/experiments/main/java/flora/experiments/sunflow/renderers/SceneRenderer_deploy.jar \
    --knobs "${PWD}/experiments/main/resources/knob/default_knobs.json" \
    --configuration "${PWD}/experiments/main/resources/configuration/default_reference_configuration.json" \
    --scene "${PWD}/experiments/main/resources/scene/gumbo_and_teapot.sc" \
    --output "${DATA_DIR}/gumbo_and_teapot.json" \
    ${PWD}/experiments/main/resources/pareto_fronts/aliens_shiny.json
