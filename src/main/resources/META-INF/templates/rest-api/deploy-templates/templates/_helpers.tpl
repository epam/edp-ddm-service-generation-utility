{{- define "keycloak.customUrl" -}}
{{- printf "%s%s%s" "https://" .Values.keycloak.customHost "/auth"}}
{{- end -}}

{{- define "keycloak.urlPrefix" -}}
{{- printf "%s%s%s" .Values.keycloak.url "/realms/" .Release.Namespace -}}
{{- end -}}

{{- define "keycloak.customUrlPrefix" -}}
{{- printf "%s%s%s" (include "keycloak.customUrl" .) "/realms/" .Release.Namespace -}}
{{- end -}}

{{- define "issuer.officer" -}}
{{- printf "%s-%s" (include "keycloak.urlPrefix" .) .Values.keycloak.realms.officer -}}
{{- end -}}

{{- define "issuer.citizen" -}}
{{- printf "%s-%s" (include "keycloak.urlPrefix" .) .Values.keycloak.realms.citizen -}}
{{- end -}}

{{- define "issuer.admin" -}}
{{- printf "%s-%s" (include "keycloak.urlPrefix" .) .Values.keycloak.realms.admin -}}
{{- end -}}

{{- define "issuer.external" -}}
{{- printf "%s-%s" (include "keycloak.urlPrefix" .) .Values.keycloak.realms.external -}}
{{- end -}}

{{- define "custom-issuer.officer" -}}
{{- printf "%s-%s" (include "keycloak.customUrlPrefix" .) .Values.keycloak.realms.officer -}}
{{- end -}}

{{- define "custom-issuer.citizen" -}}
{{- printf "%s-%s" (include "keycloak.customUrlPrefix" .) .Values.keycloak.realms.citizen -}}
{{- end -}}

{{- define "custom-issuer.admin" -}}
{{- printf "%s-%s" (include "keycloak.customUrlPrefix" .) .Values.keycloak.realms.admin -}}
{{- end -}}

{{- define "custom-issuer.external" -}}
{{- printf "%s-%s" (include "keycloak.customUrlPrefix" .) .Values.keycloak.realms.external -}}
{{- end -}}

{{- define "kongPluginIssuer.external" -}}
{{- if .Values.keycloak.customHost }}
{{- include "custom-issuer.external" . }}
{{- else }}
{{- include "issuer.external" . }}
{{- end }}
{{- end -}}

{{- define "jwksUri.officer" -}}
{{- printf "%s-%s%s" (include "keycloak.urlPrefix" .) .Values.keycloak.realms.officer .Values.keycloak.certificatesEndpoint -}}
{{- end -}}

{{- define "jwksUri.citizen" -}}
{{- printf "%s-%s%s" (include "keycloak.urlPrefix" .) .Values.keycloak.realms.citizen .Values.keycloak.certificatesEndpoint -}}
{{- end -}}

{{- define "jwksUri.admin" -}}
{{- printf "%s-%s%s" (include "keycloak.urlPrefix" .) .Values.keycloak.realms.admin .Values.keycloak.certificatesEndpoint -}}
{{- end -}}

{{- define "jwksUri.external" -}}
{{- printf "%s-%s%s" (include "keycloak.urlPrefix" .) .Values.keycloak.realms.external .Values.keycloak.certificatesEndpoint -}}
{{- end -}}

{{- define "imageRegistry" -}}
{{- if .Values.global.imageRegistry -}}
{{- printf "%s/" .Values.global.imageRegistry -}}
{{- else -}}
{{- end -}}
{{- end }}

{{- define "external.platform.paths.deprecated" -}}
  {{- range $path := (((.Values.exposeSearchConditions).platform).paths) }}
    - {{ $path }}
  {{- end }}
{{- end -}}

{{- define "external.platform.search.paths" -}}
  {{- range $path := (((.Values.exposeSearchConditions).platform).paths) }}
    - /search{{ $path }}
  {{- end }}
{{- end -}}

{{- define "external.external.paths.deprecated" -}}
  {{- range $path := (((.Values.exposeSearchConditions).external).paths) }}
    - {{ $path }}
  {{- end }}
{{- end -}}

{{- define "external.external.search.paths" -}}
  {{- range $path := (((.Values.exposeSearchConditions).external).paths) }}
    - /search{{ $path }}
  {{- end }}
{{- end -}}

{{- define "public.paths.deprecated" -}}
  {{- range $path := (((.Values.exposeSearchConditions).public).paths) }}
    - {{ $path }}
  {{- end }}
{{- end -}}

{{- define "public.search.paths" -}}
  {{- range $path := (((.Values.exposeSearchConditions).public).paths) }}
    - /search{{ $path }}
  {{- end }}
{{- end -}}

{{- define "public.swagger.paths.deprecated" -}}
  {{- range $path := (((.Values.exposeSearchConditions).public).paths) }}
    - {{ $path | replace "*" "**" }}
  {{- end }}
{{- end -}}

{{- define "public.swagger.search.paths" -}}
  {{- range $path := (((.Values.exposeSearchConditions).public).paths) }}
    - /search{{ $path | replace "*" "**" }}
  {{- end }}
{{- end -}}


{{- define "external-system-api.hostname" -}}
{{- printf "external-service-api-%s" (include "edp.hostnameSuffix" .) }}
{{- end }}

{{- define "external-system-api.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{- define "external-system-api.metaLabels" -}}
app.kubernetes.io/name: external-system-api
helm.sh/chart: {{ template "external-system-api.chart" . }}
app.kubernetes.io/instance: "{{ .Release.Name }}"
app.kubernetes.io/managed-by: "{{ .Release.Service }}"
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
app.kubernetes.io/component: app
{{- end -}}

{{- define "edp.hostnameSuffix" -}}
{{- printf "%s.%s" .Values.stageName .Values.dnsWildcard }}
{{- end }}

{{- define "edp.fullStageName" -}}
{{- printf "%s-%s" .Values.cdPipelineName .Values.cdPipelineStageName }}
{{- end -}}

{{- define "restApi.istioResources" -}}
{{- if .Values.global.registry.restApi.istio.sidecar.resources.limits.cpu }}
sidecar.istio.io/proxyCPULimit: {{ .Values.global.registry.restApi.istio.sidecar.resources.limits.cpu | quote }}
{{- else if and (not .Values.global.registry.restApi.istio.sidecar.resources.limits.cpu) .Values.global.istio.sidecar.resources.limits.cpu }}
sidecar.istio.io/proxyCPULimit: {{ .Values.global.istio.sidecar.resources.limits.cpu | quote }}
{{- end }}
{{- if .Values.global.registry.restApi.istio.sidecar.resources.limits.memory }}
sidecar.istio.io/proxyMemoryLimit: {{ .Values.global.registry.restApi.istio.sidecar.resources.limits.memory | quote }}
{{- else if and (not .Values.global.registry.restApi.istio.sidecar.resources.limits.memory) .Values.global.istio.sidecar.resources.limits.memory }}
sidecar.istio.io/proxyMemoryLimit: {{ .Values.global.istio.sidecar.resources.limits.memory | quote }}
{{- end }}
{{- if .Values.global.registry.restApi.istio.sidecar.resources.requests.cpu }}
sidecar.istio.io/proxyCPU: {{ .Values.global.registry.restApi.istio.sidecar.resources.requests.cpu | quote }}
{{- else if and (not .Values.global.registry.restApi.istio.sidecar.resources.requests.cpu) .Values.global.istio.sidecar.resources.requests.cpu }}
sidecar.istio.io/proxyCPU: {{ .Values.global.istio.sidecar.resources.requests.cpu | quote }}
{{- end }}
{{- if .Values.global.registry.restApi.istio.sidecar.resources.requests.memory }}
sidecar.istio.io/proxyMemory: {{ .Values.global.registry.restApi.istio.sidecar.resources.requests.memory | quote }}
{{- else if and (not .Values.global.registry.restApi.istio.sidecar.resources.requests.memory) .Values.global.istio.sidecar.resources.requests.memory }}
sidecar.istio.io/proxyMemory: {{ .Values.global.istio.sidecar.resources.requests.memory | quote }}
{{- end }}
{{- end -}}

{{- define "horizontalPodAutoscaler.apiVersion" }}
{{- if eq .Values.global.clusterVersion "4.9.0" }}
{{- printf "%s" "autoscaling/v2beta2" }}
{{- else }}
{{- printf "%s" "autoscaling/v2" }}
{{- end }}
{{- end }}

{{- define "externalS3.datafactoryBucket.enabled" -}}
{{- if and (hasKey .Values.global "externalS3") (hasKey .Values.global.externalS3 "buckets") (hasKey .Values.global.externalS3.buckets "datafactory") (hasKey .Values.global.externalS3.buckets.datafactory "name") .Values.global.externalS3.buckets.datafactory.name -}}
true
{{- else -}}
false
{{- end -}}
{{- end -}}

{{- define "externalS3.fileBucket.enabled" -}}
{{- if and (hasKey .Values.global "externalS3") (hasKey .Values.global.externalS3 "buckets") (hasKey .Values.global.externalS3.buckets "file") (hasKey .Values.global.externalS3.buckets.file "name") .Values.global.externalS3.buckets.file.name -}}
true
{{- else -}}
false
{{- end -}}
{{- end -}}

{{- define "datafactoryBucket.configMapName" -}}
{{- if eq (include "externalS3.datafactoryBucket.enabled" .) "true" -}}
datafactory-external-s3-bucket
{{- else -}}
{{ .Values.datafactoryceph.bucketName }}
{{- end -}}
{{- end -}}

{{- define "fileBucket.configMapName" -}}
{{- if eq (include "externalS3.fileBucket.enabled" .) "true" -}}
file-external-s3-bucket
{{- else -}}
{{ .Values.datafactoryFileCeph.bucketName }}
{{- end -}}
{{- end -}}

{{- define "datafactoryBucket.secretName" -}}
{{- if and (hasKey .Values.global "externalS3") (hasKey .Values.global.externalS3 "buckets") (hasKey .Values.global.externalS3.buckets "datafactory") (hasKey .Values.global.externalS3.buckets.datafactory "secretRef") .Values.global.externalS3.buckets.datafactory.secretRef -}}
{{ .Values.global.externalS3.buckets.datafactory.secretRef }}
{{- else -}}
{{ .Values.datafactoryceph.bucketName }}
{{- end -}}
{{- end -}}

{{- define "fileBucket.secretName" -}}
{{- if and (hasKey .Values.global "externalS3") (hasKey .Values.global.externalS3 "buckets") (hasKey .Values.global.externalS3.buckets "file") (hasKey .Values.global.externalS3.buckets.file "secretRef") .Values.global.externalS3.buckets.file.secretRef -}}
{{ .Values.global.externalS3.buckets.file.secretRef }}
{{- else -}}
{{ .Values.datafactoryFileCeph.bucketName }}
{{- end -}}
{{- end -}}

{{- /* New helpers for external S3 CA secret existence */ -}}
{{- define "externalS3.caSecretRef.exists" -}}
{{- if and (hasKey .Values.global "externalS3") (hasKey .Values.global.externalS3 "caSecretRef") .Values.global.externalS3.caSecretRef -}}
true
{{- else -}}
false
{{- end -}}
{{- end -}}
