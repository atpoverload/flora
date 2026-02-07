load("@rules_java//java:java_library.bzl", "java_library")

java_library(
    name = "eflect",
    visibility = ["//visibility:public"],
    srcs = glob(["*.java"]),
    deps = [
        "//eflect/src/main/java/eflect/data",
        "//eflect/src/main/java/eflect/data/jiffies",
        "//eflect/src/main/java/eflect/util",
    ]
)
