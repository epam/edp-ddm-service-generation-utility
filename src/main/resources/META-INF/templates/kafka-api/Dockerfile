FROM ${dockerProxyRegistry}/${globalNexusNamespace}/kafka-api-core-base-image-kraken-dev:1.8.0-KRAKEN-DEV-SNAPSHOT.5 AS builder
ARG NEXUS_USR
ARG NEXUS_PASS
WORKDIR /app/forbuild
COPY pom.xml settings.xml ./
COPY src src
RUN mvn deploy -B --settings settings.xml -npu -nsu -DskipTests=true -DaltDeploymentRepository=nexus::default::http://nexus:8081/nexus/repository/edp-maven-releases -Dartifactory.username=\$NEXUS_USR -Dartifactory.password=\$NEXUS_PASS -Dartifactory.baseUrl=http://nexus:8081/nexus -Dartifactory.releasesPath=edp-maven-releases

FROM ${dockerProxyRegistry}/openjdk:11.0.16-jre-slim AS layerbuilder
WORKDIR /app
COPY --from=builder /app/forbuild/target/kafka-api*.jar application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM ${dockerProxyRegistry}/openjdk:11.0.16-jre-slim
WORKDIR /app
COPY --from=layerbuilder app/dependencies/ ./
COPY --from=layerbuilder app/spring-boot-loader/ ./
COPY --from=layerbuilder app/snapshot-dependencies/ ./
COPY --from=layerbuilder app/application/ ./
ENTRYPOINT ["/bin/sh", "-c", "java \$JAVA_OPTS org.springframework.boot.loader.JarLauncher"]
