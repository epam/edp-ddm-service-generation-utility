global:
  disableRequestsLimits: false
  registry:
    soapApi:
      istio:
        sidecar:
          enabled: true
          resources:
            requests: {}
            limits: {}
      container:
        resources: {}
        envVars: {}
  
name: ${register}-soap-api

podAnnotations: {}

version: 1.1.0

port: 8080

service:
  port: 8080

ingress:
  required: true
  site: ${register}-soap-api

image:
  name: processing-consent-soap-api
  version: latest

restApi:
  url: http://${register}-rest-api

monitoring:
  namespace: openshift-monitoring
  prometheusScrapePath: /actuator/prometheus
  jobLabel: app

probes:
  liveness:
    path: /actuator/health/liveness
  readiness:
    path: /actuator/health/readiness

kafka:
  service: kafka-cluster-kafka-bootstrap:9093
  user: soap-api-user
  clusterName: kafka-cluster
  sslEnabled: true
<#noparse>
  sslUserKey: ${KAFKA_USER_KEYSTORE_KEY}
  sslUserCertificate: ${KAFKA_USER_KEYSTORE_CERTIFICATE}
  sslClusterCertificate: ${KAFKA_CLUSTER_TRUSTSTORE}
</#noparse>

audit:
  kafka:
    topic: audit-events
    schemaRegistryUrl: http://kafka-schema-registry:8081

keycloak:
  serviceAccount:
    secretName: keycloak-trembita-user-client-secret
    clientId: trembita-user