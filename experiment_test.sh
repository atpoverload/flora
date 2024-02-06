DATA_DIR="test-data"
mkdir "${DATA_DIR}"

MAX_HEAP_SIZE_FLAG="Xmx12G"

SCENES=experiments/main/resources/scene
SCENE_NAME=aliens_shiny

RESOURCES=experiments/main/resources

# java "-${MAX_HEAP_SIZE_FLAG}" \
    # -jar bazel-bin/experiments/main/java/flora/experiments/sunflow/renderers/SunflowRenderingProblem_deploy.jar \
    # --knobs "${PWD}/experiments/main/resources/knob/test_knobs.json" \
    # --configuration "${PWD}/experiments/main/resources/configuration/test_configuration.json" \
    # --scene "${PWD}/${SCENES}/${SCENE_NAME}.sc" \
    # --output "${DATA_DIR}/${SCENE_NAME}_results.json" \
    # --snapshot "${DATA_DIR}/${SCENE_NAME}_snapshot.json"

java "-${MAX_HEAP_SIZE_FLAG}" \
    -jar bazel-bin/experiments/main/java/flora/experiments/sunflow/renderers/SunflowRenderingReplayProblem_deploy.jar \
    --knobs "${PWD}/${RESOURCES}/knob/test_knobs.json" \
    --configuration "${PWD}/${RESOURCES}/configuration/test_configuration.json" \
    --scene "${PWD}/${SCENES}/${SCENE_NAME}.sc" \
    --output "${DATA_DIR}/${SCENE_NAME}_results2.json" \
    --snapshot "${DATA_DIR}/${SCENE_NAME}_snapshot2.json" \
    --reference_snapshot "${DATA_DIR}/${SCENE_NAME}_snapshot.json"
