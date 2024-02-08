DATA_DIR="test-data"
mkdir "${DATA_DIR}"

MAX_HEAP_SIZE_FLAG="Xmx12G"

SCENES="experiments/main/resources/scene"
RESOURCES="experiments/main/resources"

SUNFLOW="${PWD}/bazel-bin/experiments/main/java/flora/experiments/sunflow/renderers/SunflowRenderingProblem_deploy.jar"
KNOBS="${PWD}/${RESOURCES}/knob/test_knobs.json"
CONFIGURATION="${PWD}/${RESOURCES}/configuration/test_configuration.json"

SCENE_NAME=aliens_shiny
OTHER_SCENE_NAME=gumbo_and_teapot

java "-${MAX_HEAP_SIZE_FLAG}" \
    -jar "${SUNFLOW}" \
    --knobs "${KNOBS}" \
    --configuration "${CONFIGURATION}"\
    --scene "${PWD}/${SCENES}/${SCENE_NAME}.sc" \
    --output "${DATA_DIR}/${SCENE_NAME}_results.json" \
    --save "${DATA_DIR}/${SCENE_NAME}_state.json"

java "-${MAX_HEAP_SIZE_FLAG}" \
    -jar "${SUNFLOW}" \
    --knobs "${KNOBS}" \
    --configuration "${CONFIGURATION}"\
    --scene "${PWD}/${SCENES}/${OTHER_SCENE_NAME}.sc" \
    --output "${DATA_DIR}/${OTHER_SCENE_NAME}_results.json" \
    --save "${DATA_DIR}/${OTHER_SCENE_NAME}_state.json"

java "-${MAX_HEAP_SIZE_FLAG}" \
    -jar "${SUNFLOW}" \
    --knobs "${KNOBS}" \
    --configuration "${CONFIGURATION}"\
    --scene "${PWD}/${SCENES}/${SCENE_NAME}.sc" \
    --output "${PWD}/${DATA_DIR}/${SCENE_NAME}_replay_results.json" \
    --save "${PWD}/${DATA_DIR}/${SCENE_NAME}_replay_state.json" \
    --load "${PWD}/${DATA_DIR}/${OTHER_SCENE_NAME}_state.json"
