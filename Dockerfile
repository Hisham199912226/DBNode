# syntax=docker/dockerfile:1
FROM openjdk:8-jdk-alpine as build

RUN addgroup -S user && adduser -S user -G user
USER user

WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN --mount=type=cache,target=/root/.m2 ./mvnw install -DskipTests
RUN mkdir -p target/dependency  && (cd target/dependency ; jar -xf ../*.jar)


FROM openjdk:8-jre-alpine as Run
VOLUME /tmp
ARG DEPENDENCY=/app/target/dependency
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
EXPOSE ${SERVER_PORT}
ENTRYPOINT ["java","-cp","app:app/lib/*","com.example.dbnode.DbNodeApplication"]