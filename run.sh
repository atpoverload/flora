DATA_DIR="powercap-data"
mkdir "${DATA_DIR}"

MAX_HEAP_SIZE_FLAG="Xmx12G"

java "-${MAX_HEAP_SIZE_FLAG}" -jar bazel-bin/experiments/main/java/flora/experiments/sunflow/renderer/PowercapDriver_deploy.jar > "${DATA_DIR}/default-cornell-box.txt"
