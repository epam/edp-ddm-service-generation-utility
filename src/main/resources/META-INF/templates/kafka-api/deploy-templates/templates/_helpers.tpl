{{- define "imageRegistry" -}}
{{- if .Values.global.imageRegistry -}}
{{- printf "%s/" .Values.global.imageRegistry -}}
{{- else -}}
{{- end -}}
{{- end }}

{{- define "kafkaApi.istioResources" -}}
{{- if .Values.global.registry.kafkaApi.istio.sidecar.resources.limits.cpu }}
sidecar.istio.io/proxyCPULimit: {{ .Values.global.registry.kafkaApi.istio.sidecar.resources.limits.cpu | quote }}
{{- else if and (not .Values.global.registry.kafkaApi.istio.sidecar.resources.limits.cpu) .Values.global.istio.sidecar.resources.limits.cpu }}
sidecar.istio.io/proxyCPULimit: {{ .Values.global.istio.sidecar.resources.limits.cpu | quote }}
{{- end }}
{{- if .Values.global.registry.kafkaApi.istio.sidecar.resources.limits.memory }}
sidecar.istio.io/proxyMemoryLimit: {{ .Values.global.registry.kafkaApi.istio.sidecar.resources.limits.memory | quote }}
{{- else if and (not .Values.global.registry.kafkaApi.istio.sidecar.resources.limits.memory) .Values.global.istio.sidecar.resources.limits.memory }}
sidecar.istio.io/proxyMemoryLimit: {{ .Values.global.istio.sidecar.resources.limits.memory | quote }}
{{- end }}
{{- if .Values.global.registry.kafkaApi.istio.sidecar.resources.requests.cpu }}
sidecar.istio.io/proxyCPU: {{ .Values.global.registry.kafkaApi.istio.sidecar.resources.requests.cpu | quote }}
{{- else if and (not .Values.global.registry.kafkaApi.istio.sidecar.resources.requests.cpu) .Values.global.istio.sidecar.resources.requests.cpu }}
sidecar.istio.io/proxyCPU: {{ .Values.global.istio.sidecar.resources.requests.cpu | quote }}
{{- end }}
{{- if .Values.global.registry.kafkaApi.istio.sidecar.resources.requests.memory }}
sidecar.istio.io/proxyMemory: {{ .Values.global.registry.kafkaApi.istio.sidecar.resources.requests.memory | quote }}
{{- else if and (not .Values.global.registry.kafkaApi.istio.sidecar.resources.requests.memory) .Values.global.istio.sidecar.resources.requests.memory }}
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

{{- define "externalS3.caSecretRef.exists" -}}
{{- if and (hasKey .Values.global "externalS3") (hasKey .Values.global.externalS3 "caSecretRef") .Values.global.externalS3.caSecretRef -}}
true
{{- else -}}
false
{{- end -}}
{{- end -}}
