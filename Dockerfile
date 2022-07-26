FROM amazoncorretto:17-alpine
WORKDIR /app

RUN apk update && apk add maven

COPY . /app/
RUN mvn clean package

RUN cp target/*.jar /azplace-backend.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/azplace-backend.jar"]
