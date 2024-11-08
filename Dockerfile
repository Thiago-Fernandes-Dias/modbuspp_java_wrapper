FROM gcc:14.2 as cpp-build
WORKDIR /lib
COPY ./modbus_custom .
RUN 
RUN g++ -fPIC -shared modbuspp_interface.cpp -o modbuspp_interface.so

FROM maven:3.8.4-openjdk-11-slim AS java-build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:21-jre-slim
WORKDIR /app
COPY - from=java-build /app/target/modbuspp_java_wrapper-1.0-SNAPSHOT.jar ./
COPY - from=cpp-build /lib/modbuspp_interface.so ./
CMD ["java", "-jar", "--enable-preview", "modbuspp_java_wrapper-1.0-SNAPSHOT.jar"]
