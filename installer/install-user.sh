#!/usr/bin/env bash
set -e
APP=crm-lite
PREFIX="$HOME/.local"
APP_DIR="$PREFIX/share/$APP"
BIN_DIR="$HOME/bin"
JFX="${JFX:-$HOME/Downloads/javafx-sdk-24.0.2/lib}"

mkdir -p "$APP_DIR" "$BIN_DIR"
cp -f crm-lite.jar sqlite-jdbc-3.45.3.0.jar \
  slf4j-api-2.0.13.jar slf4j-nop-2.0.13.jar "$APP_DIR/"

cat > "$BIN_DIR/$APP" <<'LAUNCH'
#!/usr/bin/env bash
set -e
APP_DIR="$HOME/.local/share/crm-lite"
JFX="${JFX:-$HOME/Downloads/javafx-sdk-24.0.2/lib}"
exec java --enable-native-access=javafx.graphics \
          --enable-native-access=ALL-UNNAMED \
          --module-path "$JFX" --add-modules javafx.controls \
          -cp "$APP_DIR/crm-lite.jar:$APP_DIR/sqlite-jdbc-3.45.3.0.jar:$APP_DIR/slf4j-api-2.0.13.jar:$APP_DIR/slf4j-nop-2.0.13.jar" \
          app.CrmLite "$@"
LAUNCH
chmod +x "$BIN_DIR/$APP"

if ! echo "$PATH" | grep -q "$HOME/bin"; then
  echo 'export PATH="$HOME/bin:$PATH"' >> "$HOME/.zshrc"
  echo 'Added $HOME/bin to PATH. Restart Terminal or run: source ~/.zshrc'
fi
echo "Installed (user). Run with: $APP"
