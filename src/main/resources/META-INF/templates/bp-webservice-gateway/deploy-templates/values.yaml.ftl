global:
  container:
    requestsLimitsEnabled: true
  istio:
    sidecar:
      requestsLimitsEnabled: true
      resources:
        requests: {}
        limits: {}
  registry:
    bpWebserviceGateway:
      container:
        envVars: {}
        resources:
          requests: {}
          limits: {}
      hpa:
        enabled: false
        minReplicas: 1
        maxReplicas: 3
      istio:
        sidecar:
          enabled: true
          resources:
            requests: {}
            limits: {}
      replicas: 1

image:
  name: bp-webservice-gateway
  version: latest
port: 8080
service:
  type: ClusterIP
  port: 8080
appConfigMountPath: "/app/config/main"
trembitaBusinessProcessesConfigMountPath: "/app/config/trembita-business-processes"
trembitaInvokerKeycloakClientSecretsMountPath: "/app/secrets/trembita-invoker-client"
cephSecretMountPath: "/app/secrets/ceph"
readinessPath: "/actuator/health/readiness"
livenessPath: "/actuator/health/liveness"
redisSecretsMountPath: "/app/secrets/redis"

ingress: # expose the service with a route or an ingress depending on platform type
  platform: openshift # openshift or kubernetes

routes:
  - name: bp-webservice-gateway
    path: /api
  - name: bp-webservice-gateway-trembita
    path: /ws

keycloak:
  trembitaInvokerClient:
    clientName: trembita-invoker
    secretName: keycloak-trembita-invoker-client-secret
    realm: external-system
<#noparse>
ceph:
  bucketName: lowcode-form-data-storage
  host: ${CEPH_BUCKET_HOST}
</#noparse>
s3:
  config:
    client:
      protocol: http
    options:
      pathStyleAccess: true
<#noparse>
storage:
  form-data:
    type: redis
  backend:
    redis:
      password: ${REDIS_PASSWORD}
      sentinel:
        master: mymaster
        nodes: ${redis.endpoint}
</#noparse>
redis:
  secretName: redis-auth

bpmsUrl: http://bpms:8080
dsoURL: http://digital-signature-ops:8080
zipkinBaseUrl: http://zipkin.istio-system:9411

deployProfile: prod

kong:
  ingressName: kong-set-timeouts
  noPublicOidcPlugin: external-system-bp-gateway-nopublic-oidc
  route:
    rootPath: /api/gateway/business-process
    proxyPath: /

externalSystem:
  service:
    name: ext-system-api-bp-gateway

############## EDP VARIABLES TEST VALUES ##############################
namespace: name
stageName: dev
dnsWildcard: edp-epam.com

monitoring:
  namespace: openshift-monitoring
  prometheusScrapePath: /actuator/prometheus
  jobLabel: app