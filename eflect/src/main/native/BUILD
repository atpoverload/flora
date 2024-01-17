package(default_visibility = ["//visibility:public"])

genrule(
  name = "copy_link_jni_md_header",
  srcs = ["@bazel_tools//tools/jdk:jni_md_header-linux"],
  outs = ["jni_md.h"],
  cmd = "cp -f $< $@",
)

genrule(
  name = "copy_link_jni_header",
  srcs = ["@bazel_tools//tools/jdk:jni_header"],
  outs = ["jni.h"],
  cmd = "cp -f $< $@",
)


cc_library(
  name = "copy_jni_hdr_lib",
  hdrs = [
      ":copy_link_jni_header",
      ":copy_link_jni_md_header",
  ],
  includes = ["."],
)

cc_library(
    name = "cpu-scaler-lib",
    srcs = ["CPUScaler.c", "arch_spec.c", "msr.c"],
    hdrs = ["CPUScaler.h", "arch_spec.h", "msr.h"],
    deps = [":copy_jni_hdr_lib"],
    alwayslink = True,
)

cc_binary(
    name = "libCPUScaler.so",
    linkshared = True,
    data = glob(["*.h"]),
    deps = [":cpu-scaler-lib"],
)
