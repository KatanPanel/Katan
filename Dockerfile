# Compiling Katan
FROM openjdk:11 AS BUILD
ENV BUILD_HOME=/usr/katan-build/
RUN mkdir $BUILD_HOME
COPY . $BUILD_HOME

WORKDIR $BUILD_HOME
USER root
RUN ./gradlew -x test build application:shadowJar

# Production build
FROM openjdk:11-jre-alpine
ENV APPLICATION_USER 1033
RUN adduser -D -g '' $APPLICATION_USER

ENV HOME=/usr/katan

RUN mkdir $HOME
RUN mkdir $HOME/resources
RUN chown -R $APPLICATION_USER $HOME
RUN chmod -R 755 $HOME

USER $APPLICATION_USER

ENV ARTIFACT_NAME=katan-*.jar
ENV BUILD_HOME=/usr/katan-build/

COPY --from=BUILD $BUILD_HOME/application/build/libs/$ARTIFACT_NAME .
COPY --from=BUILD $BUILD_HOME/application/resources/ $HOME/resources/
WORKDIR $HOME

EXPOSE 80
EXPOSE 433
WORKDIR /usr/katan/
ENTRYPOINT exec java -jar ${ARTIFACT_NAME}