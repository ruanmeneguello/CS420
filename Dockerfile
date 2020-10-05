FROM maven

WORKDIR /opt/paymentservices

COPY . ./

RUN mvn clean package

ENTRYPOINT ["java","-jar","/opt/paymentservices/target/paymentservices-1.0-SNAPSHOT.jar"]

EXPOSE 4567

