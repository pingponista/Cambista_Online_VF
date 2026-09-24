# Multi-stage Dockerfile for api-gateway
# Stage 1: Build & Package
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder
WORKDIR /workspace

COPY pom.xml .
COPY common-proto/pom.xml common-proto/
COPY auth-service/pom.xml auth-service/
COPY exchange-rate-service/pom.xml exchange-rate-service/
COPY wallet-ledger-service/pom.xml wallet-ledger-service/
COPY transaction-service/pom.xml transaction-service/
COPY api-gateway/pom.xml api-gateway/

# Instalar POM padre y modulo transversal common-proto
RUN mvn install -N
COPY common-proto/src common-proto/src
RUN mvn install -pl common-proto -DskipTests

# Compilar y empaquetar api-gateway
COPY api-gateway/src api-gateway/src
RUN mvn package -pl api-gateway -am -DskipTests

# Stage 2: Runtime liviano JRE con usuario sin privilegios
FROM eclipse-temurin:17-jre-alpine AS runner
WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

COPY --from=builder /workspace/api-gateway/target/api-gateway-*.jar app.jar
RUN chown -R appuser:appgroup /app

USER appuser

EXPOSE 8080

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
