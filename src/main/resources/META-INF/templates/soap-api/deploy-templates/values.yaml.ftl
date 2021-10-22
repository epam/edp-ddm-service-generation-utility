global:
  disableRequestsLimits: false
  
name: ${register}-soap-api

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
  service: kafka-cluster-kafka-bootstrap:9092

audit:
  kafka:
    topic: audit-events
    schemaRegistryUrl: http://kafka-schema-registry:8081

keycloak:
  serviceAccount:
    secretName: keycloak-trembita-user-client-secret
    clientId: trembita-user
    