from eclipse-temurin:24.0.2_12-jre as builder

WORKDIR builder

ADD target/*.jar app.jar

RUN java -Djarmode=tools -jar app.jar extract --layers --destination extracted

from eclipse-temurin:24.0.2_12-jre

WORKDIR app

COPY --from=builder builder/extracted/dependencies/ ./
COPY --from=builder builder/extracted/spring-boot-loader/ ./
COPY --from=builder builder/extracted/snapshot-dependencies/ ./
COPY --from=builder builder/extracted/application/ ./

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]