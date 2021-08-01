spring:
  application:
    name: ${register}-soap-api

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
        paths: /app/secrets
