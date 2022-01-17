spring:
  application:
    name: ${register}-soap-api
  zipkin:
    baseUrl: http://zipkin.istio-system.svc:9411
    sender:
      type: web

cxf:
  path: /ws

management:
  endpoints:
    enabled-by-default: false
    web:
      exposure:
        include: "*"
    jmx:
      exposure:
        exclude: "*"
  endpoint:
    health:
      enabled: true
      show-details: always
      group:
        liveness:
          include: livenessState, livenessResponseCheck
        readiness:
          include: readinessState, webServices
    loggers:
      enabled: true
    prometheus:
      enabled: true
  health:
    livenessstate:
      enabled: true
    readinessstate:
      enabled: true
    kafka:
      enabled: false
<#noparse>
    webservices:
      readiness:
        services:
          - ${rest-api.url}/actuator/health
          - ${keycloak.server-url}
</#noparse>

probes:
  liveness:
    failureThreshold: 10

data-platform:
  keycloak:
    enabled: true

keycloak:
  serviceAccount:
    grant-type: client_credentials
