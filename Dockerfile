FROM maven

WORKDIR /opt/stedi

COPY . ./

RUN mvn clean package

ENTRYPOINT ["java","-jar","/opt/stedi/target/StepTimerWebsocket-1.0-SNAPSHOT.jar"]

EXPOSE 4567

