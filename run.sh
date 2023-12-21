DATA_DIR="data/three-objectives"
mkdir "${DATA_DIR}"

java -jar bazel-bin/experiments/main/java/flora/experiments/sunflow/renderer/Driver_deploy.jar > "${DATA_DIR}/default-cornell-box.txt"

SCENES="${PWD}"/experiments/main/java/flora/experiments/sunflow/scenes/resources
for scene in "${SCENES}"/gumbo_*.sc; do
    # echo $scene
    scene_name=$(basename "${scene}")
    java -jar bazel-bin/experiments/main/java/flora/experiments/sunflow/renderer/Driver_deploy.jar "${scene}"> "${DATA_DIR}/${scene_name}.txt"
done
