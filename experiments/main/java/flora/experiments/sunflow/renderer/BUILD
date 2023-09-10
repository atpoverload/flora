java_library(
    name = "renderer",
    srcs = glob(["*.java"]),
    deps = [
        "//experiments/main/java/flora/experiments/sunflow",
        "//experiments/main/java/flora/experiments/sunflow/image",
        "//experiments/main/java/flora/experiments/sunflow/scenes",
        "//contrib/main/java/flora/meter/contrib",
        "//src/main/java/flora",
        "//src/main/java/flora/knob",
        "//src/main/java/flora/machine",
        "//src/main/java/flora/meter",
        "//src/main/java/flora/strategy",
        "//src/main/java/flora/strategy/mab",
        "//src/main/java/flora/strategy/mab/epsilon",
        "//src/main/java/flora/strategy/mab/exploit",
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

# java_binary(
#     name = "KillingSceneRenderer",
#     runtime_deps = [
#         ":renderer",
#         "@janino//jar",
#         "@sunflow//jar",
#     ],
# )