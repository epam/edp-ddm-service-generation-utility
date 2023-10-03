<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <groupId>${basePackage}</groupId>
  <artifactId>bp-webservice-gateway</artifactId>
  <version>${fullVersion}</version>

  <properties>
    <java.version>11</java.version>
    <spring-boot-maven-plugin.version>2.7.5</spring-boot-maven-plugin.version>
    <log4j.version>2.17.0</log4j.version>
    <spring.boot.version>2.7.11</spring.boot.version>
    <spring.cloud.version>2021.0.7</spring.cloud.version>
    <bp-webservice-gateway-core-image.version>1.9.8.3</bp-webservice-gateway-core-image.version>
  </properties>

<#noparse>
 <dependencyManagement>
    <dependencies>
      <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-dependencies</artifactId>
        <version>${spring.boot.version}</version>
        <type>pom</type>
        <scope>import</scope>
      </dependency>
      <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-dependencies</artifactId>
        <version>${spring.cloud.version}</version>
        <type>pom</type>
        <scope>import</scope>
      </dependency>
      <dependency>
        <groupId>org.apache.logging.log4j</groupId>
        <artifactId>log4j-api</artifactId>
        <version>${log4j.version}</version>
      </dependency>
      <dependency>
        <groupId>org.apache.logging.log4j</groupId>
        <artifactId>log4j-core</artifactId>
        <version>${log4j.version}</version>
      </dependency>
    </dependencies>
  </dependencyManagement>

  <dependencies>
    <dependency>
      <groupId>com.epam.digital.data.platform</groupId>
      <artifactId>bp-webservice-gateway-core-image</artifactId>
      <version>${bp-webservice-gateway-core-image.version}</version>
    </dependency>
  </dependencies>
</#noparse>

  <build>
    <plugins>
    <#noparse>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>3.8.1</version>
        <configuration>
          <source>${java.version}</source>
          <target>${java.version}</target>
        </configuration>
      </plugin>
    </#noparse>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-surefire-plugin</artifactId>
        <version>2.22.2</version>
      </plugin>
      <plugin>
        <dependencies>
          <dependency>
            <groupId>javax.activation</groupId>
            <artifactId>activation</artifactId>
            <version>1.1.1</version>
          </dependency>
        </dependencies>
        <groupId>org.codehaus.mojo</groupId>
        <artifactId>jaxb2-maven-plugin</artifactId>
        <version>2.4</version>
        <executions>
          <execution>
            <id>schemagen</id>
            <phase>generate-sources</phase>
            <goals>
              <goal>schemagen</goal>
            </goals>
          </execution>
        </executions>
        <configuration>
          <sources>
            <source>
              <#noparse>${basedir}</#noparse>/src/main/java/${basePackage?replace(".", "/")}/bpwebservice/soap/dto
            </source>
            <source>
              <#noparse>${basedir}</#noparse>/src/main/java/${basePackage?replace(".", "/")}/bpwebservice/soap/factory
            </source>
          </sources>
          <outputDirectory><#noparse>${project.build.directory}</#noparse>/classes</outputDirectory>
          <transformSchemas>
            <transformSchema>
              <uri>
                https://gitbud.epam.com/mdtu-ddm/low-code-platform/platform/backend/applications/bp-webservice-gateway
              </uri>
              <toPrefix>bpws</toPrefix>
              <toFile>bp-webservice.xsd</toFile>
            </transformSchema>
          </transformSchemas>
        </configuration>
      </plugin>
      <#noparse>
      <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
        <version>${spring-boot-maven-plugin.version}</version>
        <executions>
          <execution>
            <goals>
              <goal>repackage</goal>
            </goals>
          </execution>
        </executions>
      </plugin>
      </#noparse>
    </plugins>
  </build>
</project>
