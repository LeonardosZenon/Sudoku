#!/bin/sh
# Start Keycloak in background
/opt/keycloak/bin/kc.sh start-dev --http-port=8080 &

# Wait for Keycloak to be ready (health check with timeout)
echo "Waiting for Keycloak to start on port 8080..."
for i in {1..12}; do
  if curl -f http://localhost:8080/health; then
    echo "Keycloak is ready!"
    break
  else
    echo "Keycloak is not ready yet. Waiting 5 seconds... (attempt $i/12)"
    sleep 5
  fi
done
if [ $i -eq 12 ]; then
  echo "Keycloak failed to start after 60 seconds. Exiting."
  exit 1
fi

# Start Spring Boot app on port 8081
java -jar app.jar