java_library(
    name = "toggle",
    visibility = ["//visibility:public"],
    srcs = glob(["*.java"], exclude = ["ToggleExample.java"]),
    deps = [
        "//src/main/java/flora",
        "//src/main/java/flora/knob",
        "//src/main/java/flora/work",
        "//src/main/java/flora/util",
    ],
)

java_binary(
    name = "example",
    main_class = "flora.examples.toggle.ToggleExample",
    visibility = ["//visibility:public"],
    srcs = ["ToggleExample.java"],
    deps = [
        ":toggle",
        "//src/main/java/flora",
        "//src/main/java/flora/knob",
        "//src/main/java/flora/meter",
        "//src/main/java/flora/strategy/archiving",
    ],
)