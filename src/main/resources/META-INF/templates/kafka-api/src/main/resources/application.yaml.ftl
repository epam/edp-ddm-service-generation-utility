spring:
  zipkin:
    baseUrl: http://zipkin.istio-system.svc:9411
    sender:
      type: web
  sleuth:
    opentracing:
      enabled: true
    baggage:
      correlation-enabled: true
      remote-fields: x-request-id, x-access-token, x-source-system, x-source-application, x-source-business-process,
        x-source-business-process-definition-id, x-source-business-process-instance-id,
        x-source-business-activity, x-source-business-activity-instance-id
      correlation-fields: x-request-id, x-source-system, x-source-application, x-source-business-process,
        x-source-business-process-definition-id, x-source-business-process-instance-id,
        x-source-business-activity, x-source-business-activity-instance-id

feign:
  client:
    config:
      default:
        connectTimeout: 4000
        readTimeout: 30000

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
          include: readinessState, db, webServices, kafka
    loggers:
      enabled: true
    prometheus:
      enabled: true
  health:
    livenessstate:
      enabled: true
    readinessstate:
      enabled: true
    webservices:
      readiness:
        services:
<#noparse>
          - ${dso.url}/actuator/health
          - ${keycloak.url}
</#noparse>

probes:
  liveness:
    failureThreshold: 10

<#noparse>
ceph:
  bucket: ${DATAFACTORY_CEPH_BUCKET_NAME:datafactory-ceph-bucket}

datafactory-response-ceph:
  bucket: ${DATAFACTORY_RESPONSE_CEPH_BUCKET_NAME}
</#noparse>

data-platform:
  kafka:
    producer:
      enabled: true
    consumer:
      enabled: true
      group-id: ${register}-kafka-api
      value-deserializer: org.springframework.kafka.support.serializer.ErrorHandlingDeserializer
      trusted-packages:
        - com.epam.digital.data.platform.model.core.kafka
        - ${basePackage}.model.dto
      error-handler:
        enabled-dlq: true
      custom-config:
        "[allow.auto.create.topics]": false
        "[retry.backoff.ms]": 10000
        "[spring.deserializer.value.delegate.class]": org.springframework.kafka.support.serializer.JsonDeserializer
    topics:
  <#list rootsOfTopicNames as root>
      ${root}: ${root}-${serviceVersion}-inbound
  </#list>
    max-request-size: 1000000