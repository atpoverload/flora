java_library(
    name = "ears",
    srcs = glob(["*.java"]),
    deps = [
        "//src/main/java/flora",
        "//examples/main/java/flora/examples/toggle",
        "//src/main/java/flora/knob",
        "//src/main/java/flora/knob/meta",
    ],
)

java_binary(
    name = "Driver",
    runtime_deps = [":ears"],
)
