name: ${register}-kafka-api

port: 8080

service:
  port: 8080

kafka:
  service: kafka-cluster-kafka-bootstrap:9092

db:
  secret: citus-roles-secrets
  url: citus-master
  port: 5432
  name: ${register}
  connectionTimeout: 4000
image:
  name: ${register}-kafka-api
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

dockerProxyRegistry: nexus-docker-registry.apps.cicd2.mdtu-ddm.projects.epam.com