java_library(
    name = "rendering",
    srcs = glob(["*.java"]),
    deps = [
        "//contrib/main/java/flora/contrib/ears",
        "//src/main/java/flora",
        "//src/main/java/flora/util",
        "//experiments/main/proto:knob_java_protos",
        "//experiments/main/proto:flora_rendering_problem_service_java_protos",
        "//experiments/main/proto:flora_rendering_problem_service_java_grpc",
        "@ears//jar",
        "@grpc-java//api",
        "@grpc-java//stub",
    ],
    # exports = [
    #     "//experiments/main/proto:flora_rendering_problem_service_java_protos",
    #     "//experiments/main/proto:flora_rendering_problem_service_java_grpc",
    # ]
)

java_binary(
    name = "server",
    main_class = "flora.experiments.rendering.FloraRenderingProblemServer",
    runtime_deps = [
        ":rendering",
        "@grpc-java//netty",
    ],
)
