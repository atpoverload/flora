TEST_SRCS = [
    "//src/main/java/flora",
    "//src/main/java/flora/knob",
]

java_test(
    name = "JsonSceneUtilTest",
    srcs = ["JsonSceneUtilTest.java"],
    data = glob(["*.json"]),
    deps = [
        "//experiments/main/java/flora/experiments/sunflow/scene",
        "//experiments/main/java/flora/experiments/sunflow/scene/util",
        "@maven//:org_json_json",
    ],
)
