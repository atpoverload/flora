load("@rules_java//java:java_library.bzl", "java_library")
load("@rules_java//java:java_binary.bzl", "java_binary")

java_library(
    name = "rendering",
    srcs = glob(["*.java"]),
    deps = [
        "//contrib/main/java/flora/contrib/ears",
        "//src/main/java/flora",
        "//src/main/java/flora/util",
        "//src/main/java/flora/util:json",
        "//src/main/java/flora/knob",
        "//src/main/java/flora/knob/util",
        "//src/main/java/flora/knob/util:json",
        "//experiments/main/proto:knob_java_protos",
        "//experiments/main/proto:flora_rendering_problem_service_java_protos",
        "//experiments/main/proto:flora_rendering_problem_service_java_grpc",
        # "@ears//jar",
        "//lib:ears",
        "@grpc-java//api",
        "@grpc-java//stub",
        "@maven//:org_json_json",
    ],
)

java_binary(
    name = "server",
    main_class = "flora.experiments.rendering.FloraRenderingProblemServer",
    runtime_deps = [
        ":rendering",
        "@grpc-java//netty",
    ],
)
