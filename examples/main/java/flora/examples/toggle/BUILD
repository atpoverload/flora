java_library(
    name = "toggle",
    srcs = glob(["*.java"]),
    deps = [
        "//src/main/java/flora",
        "//src/main/java/flora/context",
        "//src/main/java/flora/knob",
        "//src/main/java/flora/machine",
        "//src/main/java/flora/meter",
        "//src/main/java/flora/strategy",
    ],
)

java_binary(
    name = "ToggleMachine",
    runtime_deps = [":toggle"],
)