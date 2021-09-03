FROM mdtu-ddm-edp-cicd/rest-api-core-base-image-master:1.4.0-SNAPSHOT.20 AS builder
ARG NEXUS_USR
ARG NEXUS_PASS
WORKDIR /app/forbuild
COPY pom.xml pom.xml settings.xml ./
COPY src src
RUN find ~/.m2 -name "_remote.repositories" -exec rm -f {} \;
RUN mvn deploy -B --settings settings.xml -npu -nsu -DskipTests=true -DaltDeploymentRepository=nexus::default::http://nexus:8081/nexus/repository/edp-maven-releases -Dartifactory.username=$NEXUS_USR -Dartifactory.password=$NEXUS_PASS -Dartifactory.baseUrl=http://nexus:8081/nexus -Dartifactory.releasesPath=edp-maven-releases

FROM openjdk:11.0.7-jre-slim
WORKDIR /app
COPY --from=builder /app/forbuild/target/rest-api*.jar app.jar
CMD java $JAVA_OPTS -jar app.jar
