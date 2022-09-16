{{- define "imageRegistry" -}}
{{- if .Values.global.imageRegistry -}}
{{- printf "%s/" .Values.global.imageRegistry -}}
{{- else -}}
{{- end -}}
{{- end }}

{{- define "kafkaApi.istioResources" -}}
{{- if .Values.global.registry.kafkaApi.istio.sidecar.resources.limits.cpu }}
sidecar.istio.io/proxyCPULimit: {{ .Values.global.registry.kafkaApi.istio.sidecar.resources.limits.cpu | quote }}
{{- end }}
{{- if .Values.global.registry.kafkaApi.istio.sidecar.resources.limits.memory }}
sidecar.istio.io/proxyMemoryLimit: {{ .Values.global.registry.kafkaApi.istio.sidecar.resources.limits.memory | quote }}
{{- end }}
{{- if .Values.global.registry.kafkaApi.istio.sidecar.resources.requests.cpu }}
sidecar.istio.io/proxyCPU: {{ .Values.global.registry.kafkaApi.istio.sidecar.resources.requests.cpu | quote }}
{{- end }}
{{- if .Values.global.registry.kafkaApi.istio.sidecar.resources.requests.memory }}
sidecar.istio.io/proxyMemory: {{ .Values.global.registry.kafkaApi.istio.sidecar.resources.requests.memory | quote }}
{{- end }}
{{- end -}}