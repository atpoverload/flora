load("@rules_java//java:java_library.bzl", "java_library")

java_library(
    name = "epsilon",
    visibility = ["//visibility:public"],
    srcs = glob(["*.java"]),
    deps = [
        "//src/main/java/flora",
        "//src/main/java/flora/knob",
        "//src/main/java/flora/strategy/mab",
    ],
)