<?xml version="1.0" encoding="UTF-8"?>
<project xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xmlns="http://maven.apache.org/POM/4.0.0"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <groupId>${basePackage}</groupId>
  <artifactId>model</artifactId>
  <version>${fullVersion}</version>

    <#noparse>
      <name>model</name>
      <description>Common domain models and DTO for Kafka API and REST API</description>

      <properties>
        <model.core.version>1.9.4.3</model.core.version>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <java.version>11</java.version>
        <jackson.version>2.11.2</jackson.version>
        <javax.version>2.0.1.Final</javax.version>
      </properties>

      <dependencies>
        <dependency>
          <groupId>com.epam.digital.data.platform</groupId>
          <artifactId>model-core</artifactId>
          <version>${model.core.version}</version>
        </dependency>
        <dependency>
          <artifactId>jackson-annotations</artifactId>
          <groupId>com.fasterxml.jackson.core</groupId>
          <version>${jackson.version}</version>
        </dependency>
        <dependency>
          <artifactId>jackson-databind</artifactId>
          <groupId>com.fasterxml.jackson.core</groupId>
          <version>${jackson.version}</version>
        </dependency>
        <dependency>
          <artifactId>jackson-datatype-jsr310</artifactId>
          <groupId>com.fasterxml.jackson.datatype</groupId>
          <version>${jackson.version}</version>
        </dependency>

        <dependency>
          <groupId>javax.validation</groupId>
          <artifactId>validation-api</artifactId>
          <version>${javax.version}</version>
        </dependency>
      </dependencies>

      <build>
        <plugins>
          <plugin>
            <artifactId>maven-compiler-plugin</artifactId>
            <configuration>
              <source>
              ${java.version}</source>
              <target>${java.version}</target>
            </configuration>
            <groupId>org.apache.maven.plugins</groupId>
            <version>3.8.1</version>
          </plugin>
        </plugins>
      </build>
    </#noparse>
</project>
