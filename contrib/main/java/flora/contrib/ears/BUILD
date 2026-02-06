load("@rules_java//java:java_library.bzl", "java_library")

java_library(
    name = "ears",
    visibility = ["//visibility:public"],
    srcs = glob(["*.java"]),
    deps = [
        "//src/main/java/flora",
        "//src/main/java/flora/knob",
        "//src/main/java/flora/util",
        "//src/main/java/flora/work",
        "@ears//jar",
    ],
)
