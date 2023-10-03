FROM ${dockerProxyRegistry}/${globalNexusNamespace}/bp-webservice-gateway-core-image-1-9-8:1.9.8.3 AS builder
ARG NEXUS_USR
ARG NEXUS_PASS
WORKDIR /app/forbuild
COPY pom.xml settings.xml ./
COPY src src
USER root
RUN mvn deploy -B --settings settings.xml -npu -nsu -DskipTests=true -DaltDeploymentRepository=nexus::default::http://nexus:8081/nexus/repository/edp-maven-releases -Dartifactory.username=\$NEXUS_USR -Dartifactory.password=\$NEXUS_PASS -Dartifactory.baseUrl=http://nexus:8081/nexus -Dartifactory.releasesPath=edp-maven-releases
USER bp-webservice-gateway

FROM ${dockerProxyRegistry}/adoptopenjdk/openjdk11:alpine-jre AS bpwebservicelayer
WORKDIR /app
COPY --from=builder /app/forbuild/target//bp-webservice-gateway*.jar application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM ${dockerProxyRegistry}/adoptopenjdk/openjdk11:alpine-jre
ENV USER_UID=1001 \
    USER_NAME=bp-webservice-gateway
RUN addgroup --gid \${USER_UID} \${USER_NAME} \
     && adduser --disabled-password --uid \${USER_UID} --ingroup \${USER_NAME} \${USER_NAME}
WORKDIR /app
COPY --from=bpwebservicelayer app/dependencies/ ./
COPY --from=bpwebservicelayer app/spring-boot-loader/ ./
COPY --from=bpwebservicelayer app/snapshot-dependencies/ ./
COPY --from=bpwebservicelayer app/application/ ./
USER bp-webservice-gateway
ENTRYPOINT ["/bin/sh", "-c", "java \$JAVA_OPTS org.springframework.boot.loader.JarLauncher"]
