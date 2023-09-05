load("@rules_proto_grpc//java:defs.bzl", "java_proto_library")

proto_library(
    name = "eflect_protos",
    srcs = glob(["*.proto"]),
    deps = ["@com_google_protobuf//:timestamp_proto"]
)

java_proto_library(
    name = "eflect_java_protos",
    visibility = ["//visibility:public"],
    protos = [":eflect_protos"],
)
