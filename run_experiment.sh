DATA_DIR="sunflow-data"
mkdir "${DATA_DIR}"

MAX_HEAP_SIZE_FLAG="Xmx1G"
SUNFLOW=bazel-bin/experiments/main/java/flora/experiments/sunflow/renderers/SunflowRenderingProblem_deploy.jar

KNOBS="${PWD}/experiments/main/resources/knob/reduced_experiment_knobs.json"

SCENE="${PWD}/experiments/main/resources/scene/cornell_box_jensen.sc"
SCENE_NAME=$(basename "${SCENE}")
SCENE_NAME="${SCENE_NAME##*/}"
SCENE_NAME="${SCENE_NAME%.*}"
mkdir "${DATA_DIR}/${SCENE_NAME}"

java "-${MAX_HEAP_SIZE_FLAG}" \
    -jar "${SUNFLOW}" \
    --knobs "${KNOBS}" \
    --iterations 100 \
    --constraint 100 \
    --output "${DATA_DIR}/${SCENE_NAME}" \
    --scene "${SCENE}"
    # --configuration "${CONFIGURATION}" \
