java_library(
    name = "toggle",
    visibility = ["//visibility:public"],
    srcs = glob(["*.java"], exclude=["ToggleBandit.java", "ToggleOptimizer.java", "ToggleSummary.java"]),
    deps = [
        "//src/main/java/flora",
        "//src/main/java/flora/context",
        "//src/main/java/flora/knob",
        "//src/main/java/flora/meter",
        "//src/main/java/flora/strategy/mab",
    ],
)

java_binary(
    name = "ToggleOptimizer",
    srcs = ["ToggleOptimizer.java"],
    deps = [
        ":toggle",
        "//src/main/java/flora/strategy/mab",
        "//src/main/java/flora/strategy/mab/epsilon",
        "//src/main/java/flora/strategy/mab/exploit",
    ],
)

java_binary(
    name = "ToggleSummary",
    srcs = ["ToggleSummary.java"],
    deps = [
        ":toggle",
        "//src/main/java/flora/strategy/archiving",
    ],
)