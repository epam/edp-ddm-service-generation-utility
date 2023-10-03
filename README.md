# service-generation-utility

This console application is used for generating of Services, required to access and operate registry data.

These services are generated based on the predefined list of `Freemarker` templates placed in the `/src/main/resources/META-INF/templates` folder. 
The arguments for templates are taken from DB by `schemacrawler`.

The list of Services to be generated must be given as a program argument(s). Supports these service:
* model - library with plain dto classes, generated from DB to fit all required operations with registry db entities
* soap api - service to accept Trembita requests for data via SOAP protocol
* rest api - service to accept HTTP requests to operate data based on registry regulation (CRUD operations with entities, search operations for complex requests)
* kafka api - service to process requests from rest-api according to request-reply integration pattern
* bp-webservice-gateway -  service to accept HTTP/SOAP requests for starting business processes.

### Related components:
* PostgreSQL database - for data persistence
* liquibase-ddm-ext - creation of DB structure from registry regulation, based on which the services should be generated
* registry-regulation-publication-pipeline - pipeline to process various steps of registry regulation deployment (including service-generation-utility)

### Usage
###### mandatory arguments
* `settings` - VM option - relative path to yaml-file with registry settings, ex.: `-Dsettings=settings.yaml`

###### optional arguments
* `module` - program argument(s) - the module(s) to be generated, ex.: `[--module=model --module=kafka-api ...]`

### Local development:

#### Prerequisites:
* `setting.yaml` file is placed in the root folder, ex.:
```
settings:
  general:
    package: my.fav.registry
    register: registry
    version: 1.0
  kafka:
    retention-policy-in-days:
      read: 123456789
      write: 123456789
```

Local development is possible in 3 different modes: cluster port-forwarded (preferrable), clusterless, db-less

#### Db-less mode

**Note**: At the moment, generation is available in this mode only for `bp-webservice-gateway` service.

1. Add `--module=bp-webservice-gateway` to program run arguments
2. Add `-Dspring.profiles.active=db-less-mode -Dsettings=settings.yaml -Ddefinitions=external-system.yml` to program VM options
3. Run application with your favourite IDE or via `java -jar ...` with jar file, created above
4. `external-system.yml` - file that contains process definition identifiers and looks like:
```yaml
trembita:
  process_definitions:
    - process_definition_id: 'first-definition'
      start_vars:
        - edrpou
      return_vars: []
    - process_definition_id: 'second-definition'
      start_vars:
        - drfo
      return_vars: []
```

#### Cluster port-forwarded mode

This mode is available if the cluster with fully deployed platform and created registry exists.
Important: in this mode registry database should be already deployed and registry regulation pipeline should be run with registry db schema created from data-model configuration (data-model/main-liquibase.xml file should exist in registry-regulation gerrit repository).

1. You should connect to your registry db (svc/citus-master) via [port-forwarding](https://docs.openshift.com/container-platform/3.11/dev_guide/port_forwarding.html).
2. After successful port-forwarding to db, find secret `operational-pguser-postgres` and copy user-password value to application config (`src/main/resources/application-local.yaml`).
3. Check `src/main/resources/application-local.yaml` and customize parameter if needed (db credentials, db schema name etc.)
4. (Optional) Package application into jar file with `mvn clean package`
5. Add `--module=model --module=kafka-api --module=rest-api --module=soap-api` to program run arguments, depending on the list of Services to be generated
6. Add `-Dspring.profiles.active=local -Dsettings=settings.yaml` to program VM options
7. Run application with your favourite IDE or via `java -jar ...` with jar file, created above 

#### Clusterless mode

This mode is available when there are no accessible cluster with deployed platform. It requires more knowledge about platform and deep understanding of each step

1. Init local database (may be `docker run --name db --rm -it -d -p 5432:5432 -e POSTGRES_PASSWORD=postgres -e POSTGRES_USER=postgres -e POSTGRES_DB=postgres eeacms/citus-postgis:1.0`)
2. Execute sql scripts from `https://github.com/epam/edp-ddm-registry-postgres` (README.md describes the purpose of repo and partially advice how to run it locally, especially paying attention to run_local.sh)
3. Execute app `https://github.com/epam/edp-ddm-liquibase-ddm-ext` with passing your registry main-liquibase.xml as a parameter
4. Steps 3-7 from 'Cluster port-forwarded mode' instruction

---

After service-generation-utility is successfully finished, folders with generated java services should be created in repository root.

You can open each of them as a maven project. 

model repository should be built first (`mvn clean install`).

After that other services can be built locally.

#### rest-api

Important: cannot be run without working cluster with registry, where all required services are deployed (ceph, keycloak, dso)

1. Add `src/main/resources/application-local.yaml` file with content

```yaml
spring:
  zipkin:
    enabled: false

data-platform:
  datasource:
    url: jdbc:postgresql://localhost:5432/registry
    username: postgres
    password: postgres
  kafka:
    bootstrap: localhost:9092
    consumer:
      custom-config:
        "[fetch.max.wait.ms]": 500
    producer:
      custom-config:
        acks: all
    topic-properties:
      creation:
        num-partitions: 1
        replication-factor: 1
    ssl:
      enabled: false
  signature:
    validation:
      enabled: false
  kafka-request:
    signing:
      enabled: false
  files:
    processing:
      enabled: false
      allowed-packages: my.fav.registry.template.model.dto
  jwt:
    validation:
      enabled: false

management:
  endpoint:
    health:
      probes:
        enabled: true

logging:
  level:
    root: INFO
    org.zalando.logbook: TRACE
    org.jooq.tools.LoggerListener: TRACE
  pattern:
    #   Logging patterns inherited from Spring Boot default logging system configuration
    #   add tracing
    console: '%d{${LOG_DATEFORMAT_PATTERN}} [trace:%X{traceId}/%X{spanId}/%X{x-request-id}] ${LOG_LEVEL_PATTERN} %pid --- [%15.15t] %-40.40c{1.} : %m%n${LOG_EXCEPTION_CONVERSION_WORD}'

keycloak:
  url: https://keycloak-url/auth
  realms:
    - keycloak-realm-officer-portal

datafactoryceph:
  http-endpoint: https://ceph-url
  access-key: access-key
  secret-key: secret-key
  bucket: bucket
datafactory-response-ceph:
  http-endpoint: https://ceph-url
  access-key: access-key
  secret-key: secret-key
  bucket: bucket

storage:
  lowcode-form-form-data-storage:
    type: ceph
    backend:
      ceph:
        http-endpoint: https://ceph-url
        access-key: access-key
        secret-key: secret-key
        bucket: bucket
  lowcode-file-storage:
    type: ceph
    backend:
      ceph:
        http-endpoint: https://ceph-url
        access-key: access-key
        secret-key: secret-key
        bucket: bucket
  datafactory-form-data-storage:
    type: ceph
    backend:
      ceph:
        http-endpoint: https://ceph-url
        access-key: access-key
        secret-key: secret-key
        bucket: bucket
  datafactory-file-storage:
    type: ceph
    backend:
      ceph:
        http-endpoint: https://ceph-url
        access-key: access-key
        secret-key: secret-key
        bucket: bucket

s3:
  config:
    client:
      protocol: http
    options:
      pathStyleAccess: true

dso:
  url: https://dso-url

audit:
  kafka:
    bootstrap: localhost:9092
    topic: audit-events
    schema-registry-url: http://localhost:8081
    ssl:
      enabled: false
```

2. Replace all urls and credentials with respective ones from cluster (database, ceph, keycloak, dso etc.).
3. Run kafka locally on port 9092 (`docker run --name kafka --rm -it -d -p 2181:2181 -p 3030:3030 -p 8081-8083:8081-8083 -p 9581-9585:9581-9585 -p 9092:9092 -e ADV_HOST=127.0.0.1 lensesio/fast-data-dev:latest`) or port forward to kafka on clusters registry (`svc/kafka-cluster-kafka-bootstrap`, `svc/kafka-schema-registry`).
4. (Optional) Package application into jar file with `mvn clean package`
5. Add `-Dspring.profiles.active=local` to program VM options
6. Run application with your favourite IDE or via `java -jar ...` with jar file, created above

#### kafka-api

Important: cannot be run without working cluster with registry, where all required services are deployed (ceph, keycloak, dso)

```yaml
spring:
  zipkin:
    enabled: false

server:
  port: 8090

data-platform:
  datasource:
    url: jdbc:postgresql://localhost:5432/registry
    username: postgres
    password: postgres
  kafka:
    bootstrap: ${KAFKA_BROKER:localhost:9092}
    ssl:
      enabled: false
    producer:
      custom-config:
        acks: all
    consumer:
      custom-config:
        "[fetch.max.wait.ms]": 500
  kafka-request:
    validation:
      enabled: false
  jwt:
    validation:
      enabled: false
  auto-generated-fields:
    validation:
      enabled: false

management:
  endpoint:
    health:
      probes:
        enabled: true

logging:
  level:
    root: INFO
    org.jooq.tools.LoggerListener: TRACE
  pattern:
    #   Logging patterns inherited from Spring Boot default logging system configuration
    #   add tracing
    console: '%d{${LOG_DATEFORMAT_PATTERN}} [trace:%X{traceId}/%X{spanId}/%X{x-request-id}] ${LOG_LEVEL_PATTERN} %pid --- [%15.15t] %-40.40c{1.} : %m%n${LOG_EXCEPTION_CONVERSION_WORD}'

keycloak:
  url: https://keycloak-url
  realms:
    - realm

ceph:
  http-endpoint: https://ceph-url
  access-key: access-key
  secret-key: secret-key
  bucket: bucket

datafactory-response-ceph:
  http-endpoint: https://ceph-url
  access-key: access-key
  secret-key: secret-key
  bucket: bucket

datafactory-file-ceph:
  http-endpoint: https://ceph-url
  access-key: access-key
  secret-key: secret-key
  bucket: bucket

s3:
  config:
    client:
      protocol: http
    options:
      pathStyleAccess: true

dso:
  url: https://dso-url

audit:
  kafka:
    bootstrap: localhost:9092
    topic: audit-events
    schema-registry-url: http://localhost:8081
    ssl:
      enabled: false
```

2. Steps 2-6 from `rest-api` instruction

#### bp-webservice-api

1. Prerequisites:

* business-process-management service is configured and running;
* Ceph-storage is configured and running;
* Keycloak is configured and running;
* digital-signature-ops service is configured and running.

2. Configuration

Available properties are following:

* `bpms.url` - business process management service base url;
* `dso.url` - digital signature ops service base url;
* `ceph.http-endpoint` - ceph base url;
* `ceph.access-key` - ceph access key;
* `ceph.secret-key` - ceph secret key;
* `ceph.bucket` - ceph bucket name;
* `keycloak.url` - keycloak base url;
* `keycloak.realm` - keycloak realm name;
* `keycloak.client-id` - keycloak client identifier;
* `keycloak.client-secret` - keycloak client secret.

3. Run application:

* `java -jar <file-name>.jar`
* WSDL will be available on: `http://localhost:8080/ws/bpWebservice.wsdl`

4. Run spring boot application using 'local' profile:

* `mvn spring-boot:run -Drun.profiles=local` OR using appropriate functions of your IDE;
* `application-local.yml` is configuration file for local development.

### Code style

Google Java Style should be used  

### License
service-generation-utility is Open Source software released under the Apache 2.0 license.
