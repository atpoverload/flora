java_library(
    name = "image",
    visibility = ["//experiments/main/java/flora/experiments/sunflow:__subpackages__"],
    srcs = glob(["*.java"]),
    deps = [
        "//experiments/main/java/flora/experiments/sunflow",
        "//experiments/main/proto:piqe_service_java_protos",
        "//experiments/main/proto:piqe_service_java_grpc",
        "@grpc-java//api",
        "@grpc-java//netty",
        "//src/main/java/flora",
        "//src/main/java/flora/meter",
        "@sunflow//jar",
    ],
)
