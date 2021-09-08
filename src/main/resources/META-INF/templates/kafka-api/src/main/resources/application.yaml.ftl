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
          include: readinessState, db, webServices
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
</#noparse>

probes:
  liveness:
    failureThreshold: 10

<#noparse>
ceph:
  bucket: ${DATAFACTORY_CEPH_BUCKET_NAME:datafactory-ceph-bucket}

datafactory-response-ceph:
  bucket: ${DATAFACTORY_RESPONSE_CEPH_BUCKET_NAME}

datafactory-file-ceph:
  bucket: ${DATAFACTORY_FILE_CEPH_BUCKET_NAME}
</#noparse>

data-platform:
  kafka:
    group-id: ${register}-kafka-api
    max-request-size: 1000000
    trusted-packages:
      - com.epam.digital.data.platform.model.core.kafka
      - ${basePackage}.model.dto
    error-handler:
      initial-interval: 1500
      max-elapsede-time: 6000
      multiplier: 2
    topics:
  <#list rootsOfTopicNames as root>
      ${root}: ${root}-${serviceVersion}-inbound
  </#list>
