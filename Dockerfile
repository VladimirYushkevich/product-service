FROM openjdk:17-alpine
WORKDIR ./metric-producer
ADD /build/libs/product-service-0.0.1-SNAPSHOT.jar product-service.jar
ENV DATASOURCE_URL ""
ENTRYPOINT ["java","-Dspring.datasource.url=${DATASOURCE_URL}","-jar","product-service.jar"]