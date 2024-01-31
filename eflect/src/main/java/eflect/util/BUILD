java_library(
    name = "util",
    visibility = ["//visibility:public"],
    srcs = glob(["*.java"]),
)

java_binary(
    name = "rapl_smoke_test",
    main_class = "eflect.util.Rapl",
    visibility = ["//visibility:public"],
    runtime_deps = [
        ":util",
    ],
)
