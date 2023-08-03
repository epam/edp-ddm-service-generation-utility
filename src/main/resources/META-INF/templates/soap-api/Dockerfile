FROM ${dockerProxyRegistry}/${globalNexusNamespace}/soap-api-core-base-image-master:1.9.4.10 AS builder
ARG NEXUS_USR
ARG NEXUS_PASS
WORKDIR /app/forbuild
COPY pom.xml settings.xml ./
COPY src src
USER root
RUN mvn deploy -B --settings settings.xml -npu -nsu -DskipTests=true -DaltDeploymentRepository=nexus::default::http://nexus:8081/nexus/repository/edp-maven-releases -Dartifactory.username=\$NEXUS_USR -Dartifactory.password=\$NEXUS_PASS -Dartifactory.baseUrl=http://nexus:8081/nexus -Dartifactory.releasesPath=edp-maven-releases
USER soap-api

FROM ${dockerProxyRegistry}/adoptopenjdk/openjdk11:alpine-jre AS soaplayer
WORKDIR /app
COPY --from=builder /app/forbuild/target/soap-api*.jar application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM ${dockerProxyRegistry}/adoptopenjdk/openjdk11:alpine-jre
WORKDIR /appF
COPY --from=soaplayer app/dependencies/ ./
COPY --from=soaplayer app/spring-boot-loader/ ./
COPY --from=soaplayer app/snapshot-dependencies/ ./
COPY --from=soaplayer app/application/ ./
ENTRYPOINT ["/bin/sh", "-c", "java \$JAVA_OPTS org.springframework.boot.loader.JarLauncher"]
