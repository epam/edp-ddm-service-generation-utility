FROM ${dockerProxyRegistry}/${globalNexusNamespace}/kafka-api-core-base-image-master:1.5.0-SNAPSHOT.39 AS builder
ARG NEXUS_USR
ARG NEXUS_PASS
WORKDIR /app/forbuild
COPY pom.xml pom.xml settings.xml ./
COPY src src
RUN find ~/.m2 -name "_remote.repositories" -exec rm -f {} \\;
RUN mvn deploy -B --settings settings.xml -npu -nsu -DskipTests=true -DaltDeploymentRepository=nexus::default::http://nexus:8081/nexus/repository/edp-maven-releases -Dartifactory.username=\$NEXUS_USR -Dartifactory.password=\$NEXUS_PASS -Dartifactory.baseUrl=http://nexus:8081/nexus -Dartifactory.releasesPath=edp-maven-releases

FROM ${dockerProxyRegistry}/openjdk:11.0.7-jre-slim
WORKDIR /app
COPY --from=builder /app/forbuild/target/kafka-api*.jar app.jar
ENTRYPOINT ["/bin/sh", "-c", "java \$JAVA_OPTS -jar app.jar"]

