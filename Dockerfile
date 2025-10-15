FROM eclipse-temurin:21-jdk-alpine

LABEL authors="LeonardosZenon"
LABEL app="SudokuEngine"

WORKDIR /app

RUN apk add --no-cache bash curl tar

ENV KC_VERSION=26.0.0
RUN curl -L  \
    https://github.com/keycloak/keycloak/releases/download/${KC_VERSION}/keycloak-${KC_VERSION}.tar.gz -o keycloak.tar.gz \
    && tar -xzf keycloak.tar.gz -C /opt \
    && mv /opt/keycloak-${KC_VERSION} /opt/keycloak \
    && rm keycloak.tar.gz

COPY target/*.jar app.jar
COPY start.sh /app/start.sh

RUN chmod +x /app/start.sh

EXPOSE 8080 8081

COPY keycloak-realm/sudoku-realm.json /opt/keycloak/data/import/sudoku-realm.json

ENV KEYCLOAK_BASE_URL=http://localhost:8080
ENV KC_BOOTSTRAP_ADMIN_USERNAME=admin
ENV KC_BOOTSTRAP_ADMIN_PASSWORD=admin
ENV KEYCLOAK_IMPORT_STRATEGY=IGNORE_EXISTING
ENV KC_IMPORT=/opt/keycloak/data/import/sudoku-realm.json

# For Windows & Mac Host OS : For Linux Host OS
ENV POSTGRESQL_DATABASE_IP=host.docker.internal

ENV SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUERURI=http://localhost:8080/realms/sudoku
ENV SPRING_PROFILES_ACTIVE=docker
ENV SERVER_PORT=8081

CMD ["/app/start.sh"]