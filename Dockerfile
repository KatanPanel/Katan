# Compiling Katan
FROM openjdk:8-jdk-alpine AS TEMP_BUILD_IMAGE
ENV BUILD_HOME=/usr/katan-build/
COPY . $BUILD_HOME

WORKDIR $BUILD_HOME
USER root
RUN ./gradlew katan-bootstrap:shadowJar

# Production build
FROM openjdk:8-jdk-alpine
ENV ARTIFACT_NAME=Katan.jar
ENV BUILD_HOME=/usr/katan-build/

WORKDIR $BUILD_HOME
COPY --from=TEMP_BUILD_IMAGE $BUILD_HOME/katan-bootstrap/build/libs/$ARTIFACT_NAME .

EXPOSE 80
EXPOSE 433
WORKDIR /usr/katan/
ENTRYPOINT exec java -jar $BUILD_HOME${ARTIFACT_NAME}