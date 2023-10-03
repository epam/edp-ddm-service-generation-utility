dso:
  url: http://localhost:8081

keycloak:
  url: http://localhost:8082
  realm: test-realm
  client-id: client-id
  client-secret: client-secret

ceph:
  http-endpoint: http://localhost:8083
  access-key: access-key
  secret-key: secret-key
  bucket: bucket

s3:
  config:
    client:
      protocol: http
    options:
      pathStyleAccess: true

bpms:
  url: http://localhost:8084

storage:
  backend:
    ceph:
      access-key: access-key
      bucket: bucket
      http-endpoint: endpoint
      secret-key: key
  file-data:
    backend:
      ceph:
        access-key: key
        bucket: bucket
        http-endpoint: endpoint
        secret-key: key
    type: ceph
  form-data:
    type: ceph
  message-payload:
    type: ceph