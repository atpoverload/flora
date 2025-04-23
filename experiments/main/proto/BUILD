load("@protobuf//bazel:java_proto_library.bzl", "java_proto_library")
load("@grpc-java//:java_grpc_library.bzl", "java_grpc_library")

proto_library(
    name = "knob_protos",
    srcs = ["knob.proto"],
)

java_proto_library(
    name = "knob_java_protos",
    visibility = ["//visibility:public"],
    deps = [":knob_protos"]
)

proto_library(
    name = "flora_rendering_problem_service_protos",
    srcs = ["flora_rendering_problem_service.proto"],
    deps = [":knob_protos"]
)

java_proto_library(
    name = "flora_rendering_problem_service_java_protos",
    visibility = ["//visibility:public"],
    deps = [
        ":flora_rendering_problem_service_protos",
        ":knob_protos",
    ]
)

java_grpc_library(
    name = "flora_rendering_problem_service_java_grpc",
    visibility = ["//visibility:public"],
    srcs = [":flora_rendering_problem_service_protos"],
    deps = [
        ":flora_rendering_problem_service_java_protos",
        ":knob_java_protos",
    ]
)

# proto_library(
#     name = "piqe_service_protos",
#     srcs = ["piqe_service.proto"],
# )

# java_grpc_library(
#     name = "piqe_service_java_grpc",
#     visibility = ["//visibility:public"],
#     protos = [":piqe_service_protos"],
# )
