java_library(
    name = "util",
    visibility = ["//visibility:public"],
    srcs = glob(["*.java"], exclude = ["JsonUtil.java"]),
    deps = [
        "//src/main/java/flora",
        "//src/main/java/flora/fault",
    ]
)

java_library(
    name = "json",
    visibility = ["//visibility:public"],
    srcs = ["JsonUtil.java"],
    deps = [
        ":util",
        "@maven//:org_json_json",
    ]
)
