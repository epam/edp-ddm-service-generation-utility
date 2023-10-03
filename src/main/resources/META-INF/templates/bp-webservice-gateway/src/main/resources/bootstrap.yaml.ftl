spring:
  profiles: local
  cloud:
    kubernetes:
      config:
        enabled: false
logging:
  config: classpath:log4j2-local-console.xml

---
spring:
  application:
    name: bp-webserice-gateway
  cloud:
    kubernetes:
      config:
        enabled: true
        enable-api: false
        paths:
          - /app/config/main/application.yml
          - /app/config/trembita-business-processes/trembita-business-processes.yml
      secrets:
        enabled: true
        enable-api: false
        paths:
          - /app/secrets
logging:
  config: classpath:log4j2-json-console.xml # stdout log streaming for fluentd