java_library(
    name = "renderer",
    srcs = glob(["*.java"]),
    deps = [
        "//src/main/java/flora",
        "//src/main/java/flora/knob",
        "//src/main/java/flora/meter",
        "//src/main/java/flora/strategy",
        "//src/main/java/flora/strategy/mab",
        "//src/main/java/flora/strategy/mab/epsilon",
        "//src/main/java/flora/strategy/mab/exploit",
        "//experiments/main/java/flora/experiments/sunflow",
        "//experiments/main/java/flora/experiments/sunflow/scenes",
        "@sunflow//jar",
    ],
)

java_binary(
    name = "SceneRenderer",
    runtime_deps = [
        ":renderer",
        "@janino//jar",
        "@sunflow//jar",
    ],
)
