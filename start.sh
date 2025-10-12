##!/bin/sh
## Start Keycloak in background
#/opt/keycloak/bin/kc.sh start-dev --http-port=8080 &
#
## Wait for Keycloak to be ready (health check with timeout)
#echo "Waiting for Keycloak to start on port 8080..."
#for i in {1..12}; do
#  if curl -f http://localhost:8080/health; then
#    echo "Keycloak is ready!"
#    break
#  else
#    echo "Keycloak is not ready yet. Waiting 5 seconds... (attempt $i/12)"
#    sleep 5
#  fi
#done
#if [ $i -eq 12 ]; then
#  echo "Keycloak failed to start after 60 seconds. Exiting."
#  exit 1
#fi
#
## Start Spring Boot app on port 8081
#java -jar app.jar

#!/bin/sh
set -e

# Start Keycloak in background
/opt/keycloak/bin/kc.sh start-dev --http-port=8080 --hostname-strict=false &
KC_PID=$!

echo "[INFO] KEYCLOAK: Configured to start on port 8080..."
for i in $(seq 1 12); do
  if curl -sSf http://localhost:8080/health/ready >/dev/null 2>&1; then
    echo "[INFO] KEYCLOAK: Up & Running"
    break
  else
    echo "[INFO] KEYCLOAK: Attempt $i/12 Waiting... "
    sleep 5
  fi
done
if [ "$i" -eq 12 ]; then
  echo "[ERROR] KEYCLOAK: Failed to start after 60 seconds."
  exit 1
fi

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
  /opt/keycloak/bin/kcadm.sh set-password -r sudoku --username testuser --new-password password123

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
echo "🚀 Starting Spring Boot app..."
java -jar /app/app.jar
