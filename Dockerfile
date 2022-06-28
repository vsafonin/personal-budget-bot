FROM openjdk:17-alpine3.14
RUN mkdir -p /var/app/conf
COPY src/main/resources/application.properties /var/app/conf/
ARG FILE_PROP=/var/app/conf/application.properties
ARG SSL_ENABLE=false
ARG JAR_FILE=target/personalAccounterBot-0.0.1.jar
COPY ${JAR_FILE} /var/app/app.jar
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
ENTRYPOINT [ "java","-jar","/var/app/app.jar", "--spring.config.location=/var/app/conf/"]