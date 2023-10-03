server:
  port: 8080

platform:
  security:
    enabled: true
    whitelist:
      - /ws/**
      - /swagger
      - /v3/api-docs/**
      - /swagger-ui/**
      - /actuator/**
  logging:
    aspect:
      enabled: false
    primary-url:
      enabled: true
<#noparse>
management:
  endpoints:
    web:
      exposure:
        include: 'health'
  endpoint:
    health:
      enabled: true
      show-details: always
      group:
        liveness:
          include: livenessState
        readiness:
          include: readinessState, redis
  health:
    livenessstate:
      enabled: true
    readinessstate:
      enabled: true
    kafka:
      enabled: false
    kubernetes:
      enabled: false
    webservices:
      readiness:
        services:
          - ${keycloak.url}
</#noparse>
spring:
  cloud:
    discovery:
      client:
        health-indicator:
          enabled: false

springdoc:
  swagger-ui:
    path: "/swagger"

logbook:
  feign:
    enabled: true
  info-logging:
    enabled: true
  strategy: without-body
  exclude:
    - /actuator/**
    - /swagger/**
    - /swagger-ui/**
  obfuscate:
    headers:
      - x-access-token