load("@bazel_tools//tools/build_defs/repo:git.bzl", "git_repository")
load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive", "http_jar")

RULES_NIXPKGS_COMMIT = "c871abcedf5734513f7ab731ea6ba541636f4df6" 

http_archive(
    name = "io_tweag_rules_nixpkgs",
    strip_prefix = "rules_nixpkgs-%s" % RULES_NIXPKGS_COMMIT,
    urls = ["https://github.com/tweag/rules_nixpkgs/archive/%s.tar.gz" % RULES_NIXPKGS_COMMIT],
    sha256 = "532634d78c35a42745bc1ceb02193c1505e676ed55746947061c2b6bb37b85fb",
)

load("@io_tweag_rules_nixpkgs//nixpkgs:repositories.bzl", "rules_nixpkgs_dependencies")
rules_nixpkgs_dependencies()

load("@io_tweag_rules_nixpkgs//nixpkgs:nixpkgs.bzl", "nixpkgs_local_repository")
nixpkgs_local_repository(
  name = "nixpkgs",
  nix_flake_lock_file = "//:flake.lock",
  nix_file_deps = ["//:flake.lock"],
)

load("@io_tweag_rules_nixpkgs//nixpkgs:nixpkgs.bzl", "nixpkgs_java_configure")
nixpkgs_java_configure(
attribute_path = "jdk17.home",
repository = "@nixpkgs",
toolchain = True,
toolchain_name = "nixpkgs_java",
toolchain_version = "17",
)

# setting up rules to load deps
RULES_JVM_EXTERNAL_TAG = "4.5"
RULES_JVM_EXTERNAL_SHA ="b17d7388feb9bfa7f2fa09031b32707df529f26c91ab9e5d909eb1676badd9a6"

http_archive(
    name = "rules_jvm_external",
    sha256 = RULES_JVM_EXTERNAL_SHA,
    strip_prefix = "rules_jvm_external-%s" % RULES_JVM_EXTERNAL_TAG,
    url = "https://github.com/bazelbuild/rules_jvm_external/archive/%s.zip" % RULES_JVM_EXTERNAL_TAG,
)

load("@rules_jvm_external//:defs.bzl", "maven_install")

http_archive(
    name = "bazel_skylib",
    sha256 = "66ffd9315665bfaafc96b52278f57c7e2dd09f5ede279ea6d39b2be471e7e3aa",
    urls = [
        "https://mirror.bazel.build/github.com/bazelbuild/bazel-skylib/releases/download/1.4.2/bazel-skylib-1.4.2.tar.gz",
        "https://github.com/bazelbuild/bazel-skylib/releases/download/1.4.2/bazel-skylib-1.4.2.tar.gz",
    ],
)

load("@bazel_skylib//:workspace.bzl", "bazel_skylib_workspace")

bazel_skylib_workspace()

# grpc
# https://rules-proto-grpc.com/en/latest/#installation
http_archive(
    name = "rules_proto_grpc",
    # sha256 = "Kghgozaug2tUZxy74HEO7BfGTvcMTFqIzP1H6m43Ob0=",
    strip_prefix = "rules_proto_grpc-4.6.0",
    urls = ["https://github.com/rules-proto-grpc/rules_proto_grpc/releases/download/4.6.0/rules_proto_grpc-4.6.0.tar.gz"],
)

load("@rules_proto_grpc//:repositories.bzl", "rules_proto_grpc_toolchains", "rules_proto_grpc_repos")
rules_proto_grpc_toolchains()
rules_proto_grpc_repos()

load("@rules_proto//proto:repositories.bzl", "rules_proto_dependencies", "rules_proto_toolchains")
rules_proto_dependencies()
rules_proto_toolchains()

# java grpc
load("@rules_proto_grpc//java:repositories.bzl", rules_proto_grpc_java_repos = "java_repos")

rules_proto_grpc_java_repos()

load("@rules_jvm_external//:defs.bzl", "maven_install")
load("@io_grpc_grpc_java//:repositories.bzl", "IO_GRPC_GRPC_JAVA_ARTIFACTS", "IO_GRPC_GRPC_JAVA_OVERRIDE_TARGETS", "grpc_java_repositories")

maven_install(
    artifacts = IO_GRPC_GRPC_JAVA_ARTIFACTS + [
        "org.json:json:20231013",
        "commons-cli:commons-cli:1.7.0",
    ],
    generate_compat_repositories = True,
    override_targets = IO_GRPC_GRPC_JAVA_OVERRIDE_TARGETS,
    repositories = [
        "https://repo.maven.apache.org/maven2/",
    ],
)

load("@maven//:compat.bzl", "compat_repositories")

compat_repositories()

grpc_java_repositories()

http_archive(
    name = "rules_java",
    urls = [
        "https://github.com/bazelbuild/rules_java/releases/download/7.3.2/rules_java-7.3.2.tar.gz",
    ],
    sha256 = "3121a00588b1581bd7c1f9b550599629e5adcc11ba9c65f482bbd5cfe47fdf30",
)

load("@rules_java//java:repositories.bzl", "rules_java_dependencies", "rules_java_toolchains")
rules_java_dependencies()

rules_java_toolchains()

http_jar(
    name = "sunflow",
    urls = ["https://sunflow-deps.s3.amazonaws.com/sunflow.jar"],
)

http_jar(
    name = "janino",
    urls = ["https://sunflow-deps.s3.amazonaws.com/janino.jar"],
)

http_jar(
    name = "ears",
    sha256 = "340cffece3021fbc167046cbdb1b7d16ee14742135359a006375fffc68762b59",
    urls = [
        # "https://test.lpm.feri.um.si/ears.jar",
        "https://test.lpm.feri.um.si/ears3.jar",
    ],
)
