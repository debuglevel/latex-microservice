## Building stage
FROM openjdk:8-jdk-alpine AS builder
WORKDIR /src/

# cache gradle
COPY gradle /src/gradle
COPY gradlew /src/
# run "gradle --version" to let gradle-wrapper download gradle
RUN ./gradlew --version

# build source
COPY . /src/
RUN ./gradlew build

## Final image
FROM miktex/miktex
RUN apt-get update && \
    apt-get install -y openjdk-8-jre-headless && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY --from=builder /src/build/libs/*.jar /app/microservice.jar
COPY docker-entrypoint.sh /app/

# set the default port to 80
ENV PORT 80
EXPOSE 80

CMD ["/app/docker-entrypoint.sh"]
