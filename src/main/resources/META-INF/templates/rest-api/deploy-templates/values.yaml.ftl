name: ${register}-rest-api

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

image:
  name: ${register}-rest-api
  version: latest

ceph:
  bucketName: lowcode-form-data-storage

lowcodeFileCeph:
  bucketName: lowcode-file-storage

datafactoryceph:
  bucketName: datafactory-ceph-bucket

datafactoryResponseCeph:
  bucketName: response-ceph-bucket

datafactoryFileCeph:
  bucketName: file-ceph-bucket

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