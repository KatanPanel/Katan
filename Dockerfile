FROM adoptopenjdk/openjdk11:alpine AS TEMP_BUILD_IMAGE
ENV BUILD_HOME=/usr/katan-build/
COPY . $BUILD_HOME

WORKDIR $BUILD_HOME
USER root
RUN ./gradlew application:shadowJar

# Production build
FROM adoptopenjdk/openjdk11:alpine
ENV ARTIFACT_NAME=katan-*.jar
ENV BUILD_HOME=/usr/katan-build/
ENV HOME=/usr/katan/

WORKDIR $HOME
RUN mkdir $HOME/resources

COPY --from=TEMP_BUILD_IMAGE $BUILD_HOME/application/build/libs/ .
COPY --from=TEMP_BUILD_IMAGE $BUILD_HOME/application/build/resources/main/ .

EXPOSE 80
ENTRYPOINT exec java -jar $HOME${ARTIFACT_NAME}