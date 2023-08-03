FROM ${dockerProxyRegistry}/${globalNexusNamespace}/model-core-base-image-master:1.9.4.7 AS builder
ARG NEXUS_USR
ARG NEXUS_PASS
WORKDIR /app/forbuild
COPY pom.xml pom.xml settings.xml ./
COPY src src
USER root
RUN mvn deploy -B --settings settings.xml -npu -nsu -DskipTests=true -DaltDeploymentRepository=nexus::default::http://nexus:8081/nexus/repository/edp-maven-releases -Dartifactory.username=\$NEXUS_USR -Dartifactory.password=\$NEXUS_PASS -Dartifactory.baseUrl=http://nexus:8081/nexus -Dartifactory.releasesPath=edp-maven-releases
USER model-core

FROM ${dockerProxyRegistry}/adoptopenjdk/openjdk11:alpine-jre
WORKDIR /app
COPY --from=builder /app/forbuild/target/model*.jar app.jar
ENTRYPOINT ["/bin/sh", "-c", "java \$JAVA_OPTS -jar app.jar"]
