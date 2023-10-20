java_library(
    name = "ears",
    visibility = ["//visibility:public"],
    srcs = glob(["*.java"]),
    deps = [
        "//examples/main/java/flora/examples/toggle",
        "//src/main/java/flora",
        "//src/main/java/flora/knob",
        "//src/main/java/flora/knob/meta",
        "//src/main/java/flora/meter",
        "//src/main/java/flora/work",
        "@ears//jar",
    ],
)

java_binary(
    name = "Driver",
    runtime_deps = [":ears"],
)
