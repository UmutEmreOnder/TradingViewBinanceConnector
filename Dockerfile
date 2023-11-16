FROM maven:3.8.1-openjdk-17-slim AS build

COPY src /home/app/src
COPY pom.xml /home/app

WORKDIR /home/app

RUN mvn clean package

FROM openjdk:17-alpine3.14
COPY --from=build /home/app/target/TradingViewBinanceConnector-0.0.1-SNAPSHOT.jar TradingViewBinanceConnector-0.0.1-SNAPSHOT.jar
CMD ["java", "-jar", "TradingViewBinanceConnector-0.0.1-SNAPSHOT.jar"]
