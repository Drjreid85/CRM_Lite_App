#!/usr/bin/env bash
set -e
APP=crm-lite
PREFIX=/usr/local
APP_DIR="$PREFIX/share/$APP"
BIN_DIR="$PREFIX/bin"
JFX="${JFX:-$HOME/Downloads/javafx-sdk-24.0.2/lib}"

sudo mkdir -p "$APP_DIR"
sudo cp -f crm-lite.jar sqlite-jdbc-3.45.3.0.jar \
  slf4j-api-2.0.13.jar slf4j-nop-2.0.13.jar "$APP_DIR/"

sudo tee "$BIN_DIR/$APP" >/dev/null <<'LAUNCH'
#!/usr/bin/env bash
set -e
APP_DIR="/usr/local/share/crm-lite"
JFX="${JFX:-$HOME/Downloads/javafx-sdk-24.0.2/lib}"
exec java --enable-native-access=javafx.graphics \
          --enable-native-access=ALL-UNNAMED \
          --module-path "$JFX" --add-modules javafx.controls \
          -cp "$APP_DIR/crm-lite.jar:$APP_DIR/sqlite-jdbc-3.45.3.0.jar:$APP_DIR/slf4j-api-2.0.13.jar:$APP_DIR/slf4j-nop-2.0.13.jar" \
          app.CrmLite "$@"
LAUNCH
sudo chmod +x "$BIN_DIR/$APP"
echo "Installed. Run with: $APP"
