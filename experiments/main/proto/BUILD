load("@rules_proto_grpc//java:defs.bzl", "java_grpc_library")
load("@rules_proto_grpc//python:defs.bzl", "python_grpc_library")

proto_library(
    name = "piqe_service_protos",
    srcs = ["piqe_service.proto"],
)

java_grpc_library(
    name = "piqe_service_java_grpc",
    visibility = ["//visibility:public"],
    protos = [":piqe_service_protos"],
)

python_grpc_library(
    name = "piqe_service_python_grpc",
    visibility = ["//visibility:public"],
    protos = [":piqe_service_protos"],
)
