load("@rules_java//java:java_library.bzl", "java_library")

java_library(
    name = "contrib",
    visibility = ["//visibility:public"],
    srcs = glob(["*.java"]),
    deps = [
        "//src/main/java/flora",
        # "//eflect/src/main/java/eflect",
        # "//eflect/src/main/protobuf/eflect:eflect_java_protos",
        "//eflect_rapl/src/main/java/eflect",
        "//eflect_rapl/src/main/java/eflect/data",
    ]
)
