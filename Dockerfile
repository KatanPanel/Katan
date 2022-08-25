# Compiling Katan
FROM openjdk:11-jre-slim-buster AS TEMP_BUILD_IMAGE
ENV BUILD_HOME=/usr/katan-build/
COPY . $BUILD_HOME

WORKDIR $BUILD_HOME
USER root
RUN ./gradlew application:shadowJar

# Production build
FROM openjdk:11-jre-slim-buster
ENV ARTIFACT_NAME=katan-*.jar
ENV BUILD_HOME=/usr/katan-build/

WORKDIR $BUILD_HOME
COPY --from=TEMP_BUILD_IMAGE $BUILD_HOME/application/build/libs/$ARTIFACT_NAME .

EXPOSE 80
EXPOSE 433
WORKDIR /usr/katan/
ENTRYPOINT exec java -jar $BUILD_HOME${ARTIFACT_NAME}