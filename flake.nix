{
  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-23.11";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs = { nixpkgs, flake-utils, ... }:

    flake-utils.lib.eachDefaultSystem (system:
      let pkgs = import nixpkgs { inherit system; };
      in
      {
        devShells.default =
          pkgs.mkShellNoCC {
            name = "rules_nixpkgs_shell";

            packages = with pkgs; [ bazel_6 bazel-buildtools cacert nix git jdk17 gcc ];
          };
      });
}

