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
  service: kafka-cluster-kafka-bootstrap:9093
  user: rest-api-user
  clusterName: kafka-cluster
  sslEnabled: true
  sslCertType: PEM
<#noparse>
  sslUserKey: ${KAFKA_USER_KEYSTORE_KEY}
  sslUserCertificate: ${KAFKA_USER_KEYSTORE_CERTIFICATE}
  sslClusterCertificate: ${KAFKA_CLUSTER_TRUSTSTORE}
</#noparse>
  numPartitions: 3
  replicationFactor: ${replicationFactor?c}
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
      <#if s3Signer??>
      signerOverride: ${s3Signer}
      </#if>
    options:
      pathStyleAccess: true

<#if exposedToPlatformPaths?has_content>
externalService:
  name: ${register}-rest-api-ext
</#if>

<#if exposedToPlatformPaths?has_content || exposedToExternalPaths?has_content>
exposeSearchConditions:
  <#if exposedToPlatformPaths?has_content>
  platform:
    paths:
    <#list exposedToPlatformPaths as path>
      - ${path}
    </#list>
  </#if>
  <#if exposedToExternalPaths?has_content>
  external:
    paths:
    <#list exposedToExternalPaths as path>
      - ${path}
    </#list>
  </#if>
</#if>
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

kong:
  clientName: trembita-invoker
  secretName: keycloak-trembita-invoker-client-secret
  ingressName: kong-set-timeouts
  noPublicOidcPlugin: external-system-datafactory-nopublic-oidc
  route:
    rootPath: /api/gateway/data-factory

stageName: ${stageName}
