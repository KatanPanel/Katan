FROM eclipse-temurin:17-jdk AS TEMP_BUILD_IMAGE
LABEL MAINTAINER="Natan Vieira Do Nascimento <natan@katan.org>"
ENV BUILD_HOME=/usr/katan-build
COPY . $BUILD_HOME

WORKDIR $BUILD_HOME
USER root
RUN ./gradlew --warning-mode all application:shadowJar

# Production build
FROM eclipse-temurin:17-jdk
ENV ARTIFACT_NAME=katan.jar
ENV BUILD_HOME=/usr/katan-build
ENV HOME=/usr/katan

WORKDIR $HOME
RUN mkdir resources
COPY --from=TEMP_BUILD_IMAGE $BUILD_HOME/application/build/libs/ .
COPY --from=TEMP_BUILD_IMAGE $BUILD_HOME/application/build/resources/main/ ./resources

ENTRYPOINT exec java -jar $HOME/${ARTIFACT_NAME}