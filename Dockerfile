FROM openjdk:17-oracle
ARG JAR_FILE=target/*.jar
WORKDIR /etc/java
EXPOSE 8080/tcp
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","app.jar"]