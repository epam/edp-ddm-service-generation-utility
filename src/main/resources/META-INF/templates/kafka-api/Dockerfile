FROM ${dockerProxyRegistry}/${globalNexusNamespace}/kafka-api-core-base-image-master:1.9.4.23 AS builder
ARG NEXUS_USR
ARG NEXUS_PASS
WORKDIR /app/forbuild
COPY pom.xml settings.xml ./
COPY src src
USER root
RUN mvn deploy -B --settings settings.xml -npu -nsu -DskipTests=true -DaltDeploymentRepository=nexus::default::http://nexus:8081/nexus/repository/edp-maven-releases -Dartifactory.username=\$NEXUS_USR -Dartifactory.password=\$NEXUS_PASS -Dartifactory.baseUrl=http://nexus:8081/nexus -Dartifactory.releasesPath=edp-maven-releases
USER kafka-api

FROM ${dockerProxyRegistry}/adoptopenjdk/openjdk11:alpine-jre AS layerbuilder
WORKDIR /app
COPY --from=builder /app/forbuild/target/kafka-api*.jar application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM ${dockerProxyRegistry}/adoptopenjdk/openjdk11:alpine-jre
WORKDIR /app
COPY --from=layerbuilder app/dependencies/ ./
COPY --from=layerbuilder app/spring-boot-loader/ ./
COPY --from=layerbuilder app/snapshot-dependencies/ ./
COPY --from=layerbuilder app/application/ ./
ENTRYPOINT ["/bin/sh", "-c", "java \$JAVA_OPTS org.springframework.boot.loader.JarLauncher"]
