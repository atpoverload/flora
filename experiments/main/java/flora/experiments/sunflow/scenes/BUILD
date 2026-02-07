load("@rules_java//java:java_library.bzl", "java_library")

java_library(
    name = "scenes",
    visibility = ["//experiments/main/java/flora/experiments/sunflow:__subpackages__"],
    srcs = glob(["*.java"]),
    deps = [
        "//experiments/main/java/flora/experiments/sunflow",
        "//experiments/main/java/flora/experiments/sunflow/scene",
        "@sunflow//jar",
    ],
)
