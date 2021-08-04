FROM mdtu-ddm-edp-cicd/kafka-api-core-base-image-master:1.4.0-SNAPSHOT.2 AS builder
ARG NEXUS_USR
ARG NEXUS_PASS
WORKDIR /app/forbuild
COPY pom.xml pom.xml settings.xml ./
COPY src src
RUN mvn deploy -B --settings settings.xml -DskipTests=true -DaltDeploymentRepository=nexus::default::http://nexus:8081/nexus/repository/edp-maven-releases -Dartifactory.username=$NEXUS_USR -Dartifactory.password=$NEXUS_PASS -Dartifactory.baseUrl=http://nexus:8081/nexus -Dartifactory.releasesPath=edp-maven-releases

FROM openjdk:11.0.7-jre-slim
WORKDIR /app
COPY --from=builder /app/forbuild/target/kafka-api*.jar app.jar
CMD java $JAVA_OPTS -jar app.jar