{{- define "keycloak.customUrl" -}}
{{- printf "%s%s" "https://" .Values.keycloak.customHost }}
{{- end -}}

{{- define "keycloak.urlPrefix" -}}
{{- printf "%s%s%s" .Values.keycloak.url "/auth/realms/" .Release.Namespace -}}
{{- end -}}

{{- define "keycloak.customUrlPrefix" -}}
{{- printf "%s%s%s" (include "keycloak.customUrl" .) "/auth/realms/" .Release.Namespace -}}
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

{{- define "external.platform.paths" -}}
  {{- range $path := (((.Values.exposeSearchConditions).platform).paths) }}
    - {{ $path }}
  {{- end }}
{{- end -}}

{{- define "external.external.paths" -}}
  {{- range $path := (((.Values.exposeSearchConditions).external).paths) }}
    - {{ $path }}
  {{- end }}
{{- end -}}

{{- define "public.paths" -}}
  {{- range $path := (((.Values.exposeSearchConditions).public).paths) }}
    - {{ $path }}
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
{{- end }}
{{- if .Values.global.registry.restApi.istio.sidecar.resources.limits.memory }}
sidecar.istio.io/proxyMemoryLimit: {{ .Values.global.registry.restApi.istio.sidecar.resources.limits.memory | quote }}
{{- end }}
{{- if .Values.global.registry.restApi.istio.sidecar.resources.requests.cpu }}
sidecar.istio.io/proxyCPU: {{ .Values.global.registry.restApi.istio.sidecar.resources.requests.cpu | quote }}
{{- end }}
{{- if .Values.global.registry.restApi.istio.sidecar.resources.requests.memory }}
sidecar.istio.io/proxyMemory: {{ .Values.global.registry.restApi.istio.sidecar.resources.requests.memory | quote }}
{{- end }}
{{- end -}}

{{- define "horizontalPodAutoscaler.apiVersion" }}
{{- if eq .Values.global.clusterVersion "4.9.0" }}
{{- printf "%s" "autoscaling/v2beta2" }}
{{- else }}
{{- printf "%s" "autoscaling/v2" }}
{{- end }}
{{- end }}
