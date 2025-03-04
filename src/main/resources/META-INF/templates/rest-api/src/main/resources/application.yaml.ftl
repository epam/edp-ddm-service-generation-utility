spring:
  application:
    name: ${register}-rest-api
  mvc:
    throw-exception-if-no-handler-found: true
    format:
      date: yyyy-MM-dd
      date-time: yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
      time: HH:mm:ss
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
  web:
    resources:
      add-mappings: false

feign:
  client:
    config:
      default:
        connectTimeout: 4000
        readTimeout: 30000

springdoc:
  swagger-ui:
    path: /openapi
    disable-swagger-default-url: true
    urls:
      - name: all
        url: /v3/api-docs/all
<#if enumPresent>
      - name: enum
        url: /v3/api-docs/enum
</#if>
<#list entityPaths as group, paths>
      - name: ${group}
        url: /v3/api-docs/${group}
</#list>
<#if searchPaths?has_content>
      - name: search
        url: /v3/api-docs/search
</#if>
<#if nestedPaths?has_content>
      - name: nested
        url: /v3/api-docs/nested
</#if>
    urls-primary-name: all
  group-configs:
    - group: all
      paths-to-match:
        - /**
<#if enumPresent>
    - group: enum
      paths-to-match:
        - /enum/**
</#if>
<#list entityPaths as group, paths>
    - group: ${group}
      paths-to-match:
    <#list paths as path>
        - /${path}/**
    </#list>
</#list>
<#if searchPaths?has_content>
    - group: search
      paths-to-match:
    <#list searchPaths as path>
        - /${path}/**
        - /search/${path}/**
    </#list>
</#if>
<#if nestedPaths?has_content>
    - group: nested
      paths-to-match:
    <#list nestedPaths as path>
        - /${path}/**
    </#list>
</#if>

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
  response:
    codes:
      delete: 204, 400, 401, 403, 409, 412, 500, 501
      get-enum-labels: 200, 401, 500, 501
      get-enum-label: 200, 401, 404, 500, 501
      get-by-id: 200, 400, 401, 404, 500, 501
      get-multiple: 200, 400, 401, 500, 501
      post: 201, 400, 401, 403, 409, 412, 422, 500, 501
      put: 204, 400, 401, 403, 409, 412, 422, 500, 501
      put-upsert: 200, 400, 401, 403, 409, 412, 422, 500, 501

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
          include: readinessState, kafka, db, webServices, redis
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
          - ${dso.url}/actuator/health/readiness
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
  logging:
    aspect:
      enabled: false
    primary-url:
      enabled: true

logbook:
  feign:
    enabled: true
  info-logging:
    enabled: true
  strategy: without-body
  exclude:
    - /openapi
    - /v3/api-docs/**
    - /swagger-ui/**
    - /actuator/**
  obfuscate:
    headers:
      - x-access-token
      - cookie

data-platform:
  files:
    processing:
      allowed-packages:
        - com.epam.digital.data.platform.model.core
        - ${basePackage}.model.dto
  kafka:
    producer:
      enabled: true
    consumer:
      enabled: true
      group-id: ${register}-rest-api
      trusted-packages:
        - com.epam.digital.data.platform.model.core.kafka
        - ${basePackage}.model.dto
    request-reply:
      enabled: true
      topics:
      <#list rootsOfTopicNames as root>
        ${root}:
          request: ${root}-${serviceVersion}-inbound
          reply: ${root}-${serviceVersion}-outbound
      </#list>
        data-load-csv:
          request: data-load.csv.incoming
          reply: data-load.csv.outcoming
    topic-properties:
      creation:
        enabled: true
        enabled-dlq: true
      retention:
        policies:
          -
            topic-prefixes:
              - read
              - search
            days: ${retentionPolicyDaysRead?c}
          -
            topic-prefixes:
              - create
              - update
              - delete
              - upsert
            days: ${retentionPolicyDaysWrite?c}
  web:
    filters:
      exclude:
        - /openapi
        - /v3/api-docs/**
        - /swagger-ui/**
        - /actuator/**
        - /*/csv/validation
        - /v2/*/csv/validation
        - /v2/nested/*/csv/validation
        <#list searchPaths as path>
        - /search/${path}
        </#list>
