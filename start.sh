#!/bin/bash
set -e

KC_PID=0
APP_PID=0

term_handler() {
  echo "[INFO] Caught SIGTERM, stopping processes..."

  if [ "$KC_PID" -ne 0 ] && ps -p "$KC_PID" > /dev/null 2>&1; then
    echo "[INFO] Stopping Keycloak (PID $KC_PID)"
    kill -TERM "$KC_PID" 2>/dev/null || true
    pkill -P "$KC_PID" 2>/dev/null || true
  fi

  if [ "$APP_PID" -ne 0 ] && ps -p "$APP_PID" > /dev/null 2>&1; then
    echo "[INFO] Stopping Spring Boot (PID $APP_PID)"
    kill -TERM "$APP_PID" 2>/dev/null || true
    pkill -P "$APP_PID" 2>/dev/null || true
  fi

  wait "$KC_PID" 2>/dev/null || true
  wait "$APP_PID" 2>/dev/null || true
  echo "[INFO] All processes stopped. Exiting cleanly."
  exit 0
}
trap term_handler SIGTERM SIGINT

# Start Keycloak in background
/opt/keycloak/bin/kc.sh start-dev --http-port=8080 --hostname-strict=false --import-realm &
KC_LAUNCH_PID=$!

echo "[INFO] KEYCLOAK: Configured to start on port 8080..."
for i in $(seq 1 12); do
  if curl -sSf http://localhost:8080/ >/dev/null 2>&1; then
    echo "[INFO] KEYCLOAK: Up & Running"
    break
  else
    echo "[INFO] KEYCLOAK: Attempt $i/12 Waiting... "
    sleep 5
  fi
done
if [ "$i" -eq 12 ]; then
  echo "[ERROR] KEYCLOAK: Failed to start after 60 seconds."
  kill -TERM "$KC_LAUNCH_PID" 2>/dev/null || true
  exit 1
fi

# get real Keycloak Java process PID
KC_PID=$(pgrep -f "keycloak.*quarkus-run.jar" | head -n1 || true)
if [ -z "$KC_PID" ]; then
  echo "[WARN] Could not find running Keycloak Java process; fallback to launcher PID"
  KC_PID=$KC_LAUNCH_PID
fi

echo "[INFO] KEYCLOAK PID: $KC_PID"

echo "[INFO] KEYCLOAK: Applying configurations..."

/opt/keycloak/bin/kcadm.sh config credentials \
  --server http://localhost:8080 \
  --realm master \
  --user ${KC_BOOTSTRAP_ADMIN_USERNAME:-admin} \
  --password ${KC_BOOTSTRAP_ADMIN_PASSWORD:-admin}

if ! /opt/keycloak/bin/kcadm.sh get realms/sudoku >/dev/null 2>&1; then
  echo "[INFO] KEYCLOAK: Creating realm sudoku"
  /opt/keycloak/bin/kcadm.sh create realms -s realm=sudoku -s enabled=true

  echo "[INFO] KEYCLOAK: Creating testuser"
  /opt/keycloak/bin/kcadm.sh create users -r sudoku -s username=testuser -s enabled=true
  /opt/keycloak/bin/kcadm.sh set-password -r sudoku --username testuser --new-password 123

  echo "[INFO] KEYCLOAK: Creating client sudoku-api"
  /opt/keycloak/bin/kcadm.sh create clients -r sudoku \
    -s clientId=sudoku-api \
    -s enabled=true \
    -s publicClient=false \
    -s protocol=openid-connect \
    -s 'redirectUris=["http://localhost:8081/*"]' \
    -s 'webOrigins=["*"]' \
    -s secret=mysupersecret
else
  echo "[INFO] KEYCLOAK: Realm 'sudoku' already exists, skipping creation."
fi

# --- Start Spring Boot ---
echo "[INFO] SPRINGBOOT: Starting Spring Boot app..."
java -jar /app/app.jar &
APP_PID=$!

echo "[INFO] SPRINGBOOT PID: $APP_PID"

# --- Wait for processes ---
echo "[INFO] Waiting for Keycloak (PID $KC_PID) and Spring Boot (PID $APP_PID)..."
wait -n "$KC_PID" "$APP_PID"

echo "[WARN] One of the processes has exited — cleaning up..."
term_handler
