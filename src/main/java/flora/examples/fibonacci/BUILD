java_library(
    name = "fibonacci",
    srcs = glob(["*.java"]),
    deps = [
        "//src/main/java/flora",
        "//src/main/java/flora/knob",
        "//src/main/java/flora/meter",
        "//src/main/java/flora/strategy",
    ],
)

java_binary(
    name = "FibonacciMachine",
    runtime_deps = [":fibonacci"],
)