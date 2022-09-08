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
  javaOpts: -Xms1200m -Xmx1200m -XX:+AlwaysPreTouch -XX:+UseG1GC -XX:+ExplicitGCInvokesConcurrent

kafka:
  service: kafka-cluster-kafka-bootstrap:9093
  user: kafka-api-user
  clusterName: kafka-cluster
  sslEnabled: true
  sslCertType: PEM
<#noparse>
  sslUserKey: ${KAFKA_USER_KEYSTORE_KEY}
  sslUserCertificate: ${KAFKA_USER_KEYSTORE_CERTIFICATE}
  sslClusterCertificate: ${KAFKA_CLUSTER_TRUSTSTORE}
</#noparse>
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
</#noparse>

s3:
  config:
    client:
      protocol: http
      <#if s3Signer??>
      signerOverride: ${s3Signer}
      </#if>
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