global:
  disableRequestsLimits: false

name: ${register}-kafka-api

port: 8080

service:
  port: 8080

restApi:
  service:
    name: ${register}-rest-api
    port: 8080

java:
  javaOpts: -Xms380m -Xmx380m -Xmn230m -XX:+AlwaysPreTouch -XX:+UseG1GC -XX:+ExplicitGCInvokesConcurrent -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.local.only=false -Dcom.sun.management.jmxremote.port=9010 -Dcom.sun.management.jmxremote.rmi.port=9010 -Djava.rmi.server.hostname=127.0.0.1

kafka:
  service: kafka-cluster-kafka-bootstrap:9092
  user: kafka-api-user
  clusterName: kafka-cluster
  sslEnabled: false
  consumerConfigs:
    "[fetch.max.wait.ms]": 500
  producerConfigs:
    acks: all

db:
  secret: citus-roles-secrets
  url: citus-master
  port: 5432
  name: ${register}
  connectionTimeout: 4000
image:
  name: ${register}-kafka-api
  version: latest

<#noparse>
datafactoryceph:
  bucketName: datafactory-ceph-bucket
  httpEndpoint: ${DATAFACTORY_CEPH_BUCKET_HOST}

datafactoryResponseCeph:
  bucketName: response-ceph-bucket
  httpEndpoint: ${DATAFACTORY_RESPONSE_CEPH_BUCKET_HOST}

datafactoryFileCeph:
  bucketName: file-ceph-bucket
  httpEndpoint: ${DATAFACTORY_FILE_CEPH_BUCKET_HOST}
</#noparse>

s3:
  config:
    client:
      protocol: http
    options:
      pathStyleAccess: true

dso:
  url: http://digital-signature-ops:8080

monitoring:
  namespace: openshift-monitoring
  prometheusScrapePath: /actuator/prometheus
  jobLabel: app

probes:
  liveness:
    path: /actuator/health/liveness
  readiness:
    path: /actuator/health/readiness

audit:
  kafka:
    topic: audit-events
    schemaRegistryUrl: http://kafka-schema-registry:8081

dockerProxyRegistry: nexus-docker-registry.apps.cicd2.mdtu-ddm.projects.epam.com
