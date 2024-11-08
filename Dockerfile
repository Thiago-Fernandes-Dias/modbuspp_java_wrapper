FROM gcc:14.2.0 as cpp-build
WORKDIR /lib
COPY ./modbuspp_custom .
RUN 
RUN g++ -fPIC -shared modbuspp_interface.cpp -o modbuspp_interface.so

FROM maven:3.9.9-eclipse-temurin-21 AS java-build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn package -DskipTests

FROM ubuntu:24.04
WORKDIR /app
RUN apt update
RUN apt upgrade -y
RUN apt install openjdk-21-jdk -y
COPY --from=java-build /app/target/modbuspp_java_wrapper-1.0-SNAPSHOT.jar ./
COPY --from=cpp-build /lib/modbuspp_interface.so ./
CMD ["java", "-jar", "--enable-preview", "modbuspp_java_wrapper-1.0-SNAPSHOT.jar"]
