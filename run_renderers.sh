# DATA_DIR="powercap-data"
# mkdir "${DATA_DIR}"

MAX_HEAP_SIZE_FLAG="Xmx12G"

#java "-${MAX_HEAP_SIZE_FLAG}" -jar bazel-bin/experiments/main/java/flora/experiments/sunflow/renderer/PowercapDriver_deploy.jar > "${DATA_DIR}/default-cornell-box.txt"
# java "-${MAX_HEAP_SIZE_FLAG}" -Djava.library.path=bazel-bin/eflect_rapl/src/main/native -jar bazel-bin/experiments/main/java/flora/experiments/sunflow/renderer/Driver_deploy.jar > "${DATA_DIR}/default-cornell-box.txt"

# LIB_CPUSCALER=bazel-bin/eflect_rapl/src/main/native
# java "-${MAX_HEAP_SIZE_FLAG}" -Djava.library.path="${LIB_CPUSCALER}" \
java "-${MAX_HEAP_SIZE_FLAG}" \
    -jar bazel-bin/experiments/main/java/flora/experiments/sunflow/renderers/SceneRenderer_deploy.jar \
    --output "${PWD}/test_test_data.json" \
    --scene "${PWD}/experiments/main/resources/scene/cornell_box_jensen.sc" \
    --knobs "${PWD}/experiments/main/resources/knob/ears_test_knobs.json" \
    --configuration "${PWD}/experiments/main/resources/configuration/ears_reference.json" \
    ${PWD}/experiments/main/resources/configuration/test_configurations*.json

# java "-${MAX_HEAP_SIZE_FLAG}" -Djava.library.path="${LIB_CPUSCALER}" \
#     -jar bazel-bin/experiments/main/java/flora/experiments/sunflow/renderers/SunflowRenderingProblem_deploy.jar \
#     --output "${PWD}/test_data.json" \
#     --knobs "${PWD}/experiments/main/resources/knob/ears_knobs.json" \
#     --configuration "${PWD}/experiments/main/resources/configuration/ears_reference.json"
