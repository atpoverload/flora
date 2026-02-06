load("@rules_java//java:java_library.bzl", "java_library")

java_library(
    name = "testing",
    visibility = ["//visibility:public"],
    srcs = glob(["*.java"]),
    deps = [
        "//src/main/java/flora",
        "//src/main/java/flora/meter",
    ],
)