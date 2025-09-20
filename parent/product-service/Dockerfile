from eclipse-temurin:24.0.2_12-jre as builder

workdir build

copy target/*.jar app.jar

run java -Djarmode=layertools -jar app.jar extract

from eclipse-temurin:24.0.2_12-jre

workdir app

copy --from=builder build/dependencies/ ./
copy --from=builder build/spring-boot-loader/ ./
copy --from=builder build/snapshot-dependencies/ ./
copy --from=builder build/application/ ./

expose 8080

entrypoint ["java", "org.springframework.boot.loader.JarLauncher"]
