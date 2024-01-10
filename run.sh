DATA_DIR="powercap-data"
mkdir "${DATA_DIR}"

MAX_HEAP_SIZE_FLAG="Xmx12G"

#java "-${MAX_HEAP_SIZE_FLAG}" -jar bazel-bin/experiments/main/java/flora/experiments/sunflow/renderer/PowercapDriver_deploy.jar > "${DATA_DIR}/default-cornell-box.txt"
java "-${MAX_HEAP_SIZE_FLAG}" -Djava.library.path=bazel-bin/eflect_rapl/src/main/native -jar bazel-bin/experiments/main/java/flora/experiments/sunflow/renderer/Driver_deploy.jar > "${DATA_DIR}/default-cornell-box.txt"
