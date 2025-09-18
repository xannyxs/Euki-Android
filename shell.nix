{ ... }:

let
  pinnedPkgs =
    import (builtins.fetchTarball "https://github.com/NixOS/nixpkgs/archive/nixos-25.05.tar.gz")
      { config.allowUnfree = true; };
in
pinnedPkgs.mkShell {
  buildInputs = with pinnedPkgs; [
    # App Tools
    gradle
    android-studio
    android-tools

    ktlint
    kotlin-language-server

    jdk17
    jdt-language-server
    google-java-format

    # Additional Packages
    nil
  ];
  shellHook = ''
    echo "Android development environment loaded"
    echo "Java version: $(java -version 2>&1 | head -1)"
    echo "Gradle version: $(gradle --version | grep Gradle)"
  '';

}
