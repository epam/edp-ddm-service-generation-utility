global:
  disableRequestsLimits: false
  
name: ${register}-rest-api

port: 8080

service:
  port: 8080

java:
  javaOpts: -Xms380m -Xmx380m -Xmn230m -XX:+AlwaysPreTouch -XX:+UseG1GC -XX:+ExplicitGCInvokesConcurrent

ingress:
  required: true
  site: ${register}-rest-api

kafka:
  service: kafka-cluster-kafka-bootstrap:9092
  user: rest-api-user
  clusterName: kafka-cluster
  sslEnabled: false
  numPartitions: 3
  replicationFactor: ${replicationFactor}
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
  name: ${register}-rest-api
  version: latest

<#noparse>
ceph:
  bucketName: lowcode-form-data-storage
  httpEndpoint: ${CEPH_BUCKET_HOST}

lowcodeFileCeph:
  bucketName: lowcode-file-storage
  httpEndpoint: ${LOWCODE_FILE_CEPH_BUCKET_HOST}

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

keycloak:
  rulesEnabled: true
  realms:
    officer: officer-portal
    citizen: citizen-portal
    external: external-system
  certificatesEndpoint: /protocol/openid-connect/certs
