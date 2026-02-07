load("@rules_java//java:java_library.bzl", "java_library")

THIRD_PARTY = ["JsonKnobUtil.java"]

java_library(
    name = "util",
    visibility = ["//visibility:public"],
    srcs = glob(["*.java"], exclude = THIRD_PARTY),
    deps = [
        "//src/main/java/flora",
        "//src/main/java/flora/knob",
    ],
)

java_library(
    name = "json",
    visibility = ["//visibility:public"],
    srcs = ["JsonKnobUtil.java"],
    deps = [
        "//src/main/java/flora",
        "//src/main/java/flora/knob",
        "@maven//:org_json_json",
    ],
)
