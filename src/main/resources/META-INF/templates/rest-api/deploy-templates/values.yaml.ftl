global:
  container:
    requestsLimitsEnabled: true
  istio:
    sidecar:
      requestsLimitsEnabled: true
      resources:
        requests: {}
        limits: {}
  deploymentMode: development
  registry:
    restApi:
      container:
        envVars: {}
        resources:
          requests: {}
          limits: {}
      datasource:
        maxPoolSize: 10
      kafka:
        timeoutInSeconds: 30
      hpa:
        enabled: false
        minReplicas: 1
        maxReplicas: 3
      istio:
        sidecar:
          enabled: true
          resources:
            requests: {}
            limits: {}
      replicas: 1

name: ${register}-rest-api

podAnnotations: {}

port: 8080

service:
  port: 8080

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

<#if exposedToPlatformInfo.paths?has_content || exposedToExternalInfo.paths?has_content>
externalService:
  name: ${register}-rest-api-ext
</#if>

<#if exposedToPublicInfo.paths?has_content>
publicService:
  name: ${register}-rest-api-public
</#if>

<#if exposedToPlatformInfo.paths?has_content || exposedToExternalInfo.paths?has_content || exposedToPublicInfo.paths?has_content>
exposeSearchConditions:
  <#if exposedToPlatformInfo.paths?has_content>
  platform:
    paths:
    <#list exposedToPlatformInfo.paths as path>
      - ${path}
    </#list>
    <#if exposedToPlatformInfo.anyResponseContainsFile>
      - /files/*
    </#if>
  </#if>
  <#if exposedToExternalInfo.paths?has_content>
  external:
    paths:
    <#list exposedToExternalInfo.paths as path>
      - ${path}
    </#list>
    <#if exposedToExternalInfo.anyResponseContainsFile>
      - /files/*
    </#if>
  </#if>
  <#if exposedToPublicInfo.paths?has_content>
  public:
    paths:
    <#list exposedToPublicInfo.paths as path>
      - ${path}
    </#list>
    <#if exposedToPublicInfo.anyResponseContainsFile>
      - /files/*
    </#if>
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
    admin: admin
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

redis:
  secretName: redis-auth

edpComponent:
  description: "Веб-інтерфейс для перегляду API-документації Підсистеми управління даними реєстру з ціллю подальшого використання при побудові взаємодії через типові розширення-конектори у бізнес-процесах."
  displayName: "API документація сервісу управління даними реєстру (Swagger)"
  operationalZone: "registry-administration-zone"

publicApiEdpComponent:
  name: "swagger-public-api"
  description: "Веб-інтерфейс для перегляду API-документації Підсистеми управління даними реєстру для публічних даних з ціллю подальшого використання при побудові взаємодії з неавтентифікованими користувачами та зовнішніми системами"
  displayName: "API документація сервісу управління даними реєстру (Swagger) для публічних даних"
  operationalZone: "registry-administration-zone"
