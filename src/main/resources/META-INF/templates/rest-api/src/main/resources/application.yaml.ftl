spring:
  application:
    name: ${register}-rest-api
  mvc:
    throw-exception-if-no-handler-found: true
  resources:
    add-mappings: false
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
    web:
      filter-order: -2147483648 # HIGHEST_PRECEDENCE

springdoc:
  swagger-ui:
    path: /openapi

openapi:
  request:
    headers:
      - X-Access-Token
      - X-Digital-Signature
      - X-Digital-Signature-Derived
      - X-Source-System
      - X-Source-Application
      - X-Source-Business-Process
      - X-Source-Business-Process-Definition-Id
      - X-Source-Business-Process-Instance-Id
      - X-Source-Business-Activity
      - X-Source-Business-Activity-Instance-Id

    groups:
<#if enumPresent>
      enum:
        - enum
</#if>
<#list entityPaths as group, paths>
      ${group}:
  <#list paths as path>
        - ${path}
  </#list>
</#list>
<#if searchPaths?has_content>
      search:
<#list searchPaths as path>
        - ${path}
</#list>
</#if>

  response:
    codes:
      delete: 204, 400, 401, 403, 409, 412, 500, 501
      get-enum-labels: 200, 401, 500, 501
      get-enum-label: 200, 401, 404, 500, 501
      get-by-id: 200, 400, 401, 404, 500, 501
      get-multiple: 200, 400, 401, 500, 501
      post: 201, 400, 401, 403, 409, 412, 422, 500, 501
      put: 204, 400, 401, 403, 409, 412, 422, 500, 501

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
          include: readinessState, webServices, kafka
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
  bucket: ${CEPH_BUCKET_NAME:bucket}
lowcode-file-ceph:
  bucket: ${LOWCODE_FILE_CEPH_BUCKET_NAME}
datafactoryceph:
  bucket: ${DATAFACTORY_CEPH_BUCKET_NAME:datafactory-ceph-bucket}
datafactory-response-ceph:
  bucket: ${DATAFACTORY_RESPONSE_CEPH_BUCKET_NAME}
datafactory-file-ceph:
  bucket: ${DATAFACTORY_FILE_CEPH_BUCKET_NAME}
</#noparse>

platform:
  security:
    enabled: true
    whitelist:
      - /openapi
      - /v3/api-docs/**
      - /swagger-ui/**
      - /actuator/**

logbook:
  secure-filter:
    enabled: false
  exclude:
    - /openapi
    - /v3/api-docs/**
    - /swagger-ui/**
    - /actuator/**

data-platform:
  kafka:
    group-id: ${register}-rest-api
    trusted-packages:
      - com.epam.digital.data.platform.model.core.kafka
      - ${basePackage}.model.dto
    error-handler:
      initial-interval: 1500
      max-elapsed-time: 6000
      multiplier: 2
    topic-properties:
      retention-policy-in-days:
        read: ${retentionPolicyDaysRead?c}
        write: ${retentionPolicyDaysWrite?c}
    topics:
  <#list rootsOfTopicNames as root>
      ${root}:
        request: ${root}-${serviceVersion}-inbound
        replay: ${root}-${serviceVersion}-outbound
  </#list>
