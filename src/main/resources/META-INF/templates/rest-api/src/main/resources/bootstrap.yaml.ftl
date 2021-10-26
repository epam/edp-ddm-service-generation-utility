spring:
  application:
    name: ${register}-rest-api

logging:
  config: classpath:log4j2-json-console.xml
---
spring:
  cloud:
    kubernetes:
      config:
        enabled: true
        paths: /app/config/config.yaml
        enable-api: false
      secrets:
        enabled: true
        enable-api: false
        paths:
          - /app/secrets
          - /app/secrets/ceph
          - /app/secrets/lowcodefileceph
          - /app/secrets/datafactoryceph
          - /app/secrets/datafactoryresponseceph
          - /app/secrets/datafactoryfileceph
          - /app/secrets/kafkausercertificate
          - /app/secrets/kafkaclustercertificate