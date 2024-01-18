RENDERER_SRCS = [
    "SunflowRenderingProblem.java",
    "ReferenceSceneRenderer.java",
    "SceneRenderer.java",
]

java_library(
    name = "renderers",
    srcs = glob(["*.java"], exclude = RENDERER_SRCS),
    deps = [
        "//contrib/main/java/flora/contrib/eflect",
        "//eflect/src/main/java/eflect",
        "//eflect/src/main/java/eflect/util",
        "//experiments/main/java/flora/experiments/sunflow/image",
        "//experiments/main/java/flora/experiments/sunflow/scene",
        "//experiments/main/java/flora/experiments/sunflow/scene/util",
        "//experiments/main/java/flora/experiments/sunflow/scenes",
        "//src/main/java/flora",
        "//src/main/java/flora/meter",
        "//src/main/java/flora/knob",
        "//src/main/java/flora/knob/util",
        "//src/main/java/flora/util",
        "@maven//:commons_cli_commons_cli",
        "@maven//:org_json_json",
        "@sunflow//jar",
    ],
)

java_binary(
    name = "SceneRenderer",
    srcs = ["SceneRenderer.java"],
    deps = [
        ":renderers",
        "//experiments/main/java/flora/experiments/sunflow/image",
        "//experiments/main/java/flora/experiments/sunflow/scene",
        "//experiments/main/java/flora/experiments/sunflow/scene/util",
        "//experiments/main/java/flora/experiments/sunflow/scenes",
        "//src/main/java/flora",
        "//src/main/java/flora/util",
        "//src/main/java/flora/util:json",
        "@janino//jar",
        "@maven//:commons_cli_commons_cli",
        "@maven//:org_json_json",
        "@sunflow//jar",
    ],
    data = [
        "//eflect/src/main/native:libCPUScaler.so",
    ],
)

java_binary(
    name = "SunflowRenderingProblem",
    srcs = ["SunflowRenderingProblem.java"],
    deps = [
        ":renderers",
        "//contrib/main/java/flora/contrib/ears",
        "//experiments/main/java/flora/experiments/sunflow/image",
        "//experiments/main/java/flora/experiments/sunflow/scene",
        "//experiments/main/java/flora/experiments/sunflow/scene/util",
        "//experiments/main/java/flora/experiments/sunflow/scenes",
        "//src/main/java/flora",
        "//src/main/java/flora/util",
        "//src/main/java/flora/util:json",
        "@ears//jar",
        "@janino//jar",
        "@maven//:commons_cli_commons_cli",
        "@maven//:org_json_json",
        "@sunflow//jar",
    ],
    data = [
        "//eflect/src/main/native:libCPUScaler.so",
    ],
)