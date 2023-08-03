{{- define "keycloak.urlPrefix" -}}
{{- printf "%s%s%s" .Values.keycloak.url "/realms/" .Release.Namespace -}}
{{- end -}}

{{- define "jwksUri.officer" -}}
{{- printf "%s-%s%s" (include "keycloak.urlPrefix" .) .Values.keycloak.officerClient.realm .Values.keycloak.certificatesEndpoint -}}
{{- end -}}
