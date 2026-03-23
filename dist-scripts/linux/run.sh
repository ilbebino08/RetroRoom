#!/usr/bin/env sh
DIR="$(cd "$(dirname "$0")" && pwd)"
java --module-path "$DIR/libs" --add-modules javafx.controls,javafx.fxml -jar "$DIR/retroroom-1.0-linux.jar"