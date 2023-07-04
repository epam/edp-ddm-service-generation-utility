FROM ${dockerProxyRegistry}/${globalNexusNamespace}/rest-api-core-base-image-master:1.9.6.9 AS builder
ARG NEXUS_USR
ARG NEXUS_PASS
WORKDIR /app/forbuild
COPY pom.xml settings.xml ./
COPY src src
USER root
RUN mvn deploy -B --settings settings.xml -npu -nsu -DskipTests=true -DaltDeploymentRepository=nexus::default::http://nexus:8081/nexus/repository/edp-maven-releases -Dartifactory.username=\$NEXUS_USR -Dartifactory.password=\$NEXUS_PASS -Dartifactory.baseUrl=http://nexus:8081/nexus -Dartifactory.releasesPath=edp-maven-releases
USER rest-api

FROM ${dockerProxyRegistry}/adoptopenjdk/openjdk11:alpine-jre AS restlayer
WORKDIR /app
COPY --from=builder /app/forbuild/target/rest-api*.jar application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM ${dockerProxyRegistry}/adoptopenjdk/openjdk11:alpine-jre
WORKDIR /app
COPY --from=restlayer app/dependencies/ ./
COPY --from=restlayer app/spring-boot-loader/ ./
COPY --from=restlayer app/snapshot-dependencies/ ./
COPY --from=restlayer app/application/ ./
ENTRYPOINT ["/bin/sh", "-c", "java \$JAVA_OPTS org.springframework.boot.loader.JarLauncher"]