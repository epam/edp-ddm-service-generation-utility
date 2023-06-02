{{- define "keycloak.url" -}}
{{- printf "%s%s" "https://" .Values.keycloak.host -}}
{{- end -}}

{{- define "keycloak.urlPrefix" -}}
{{- printf "%s%s%s" (include "keycloak.url" .) "/auth/realms/" .Release.Namespace -}}
{{- end -}}

{{- define "jwksUri.officer" -}}
{{- printf "%s-%s%s" (include "keycloak.urlPrefix" .) .Values.keycloak.officerClient.realm .Values.keycloak.certificatesEndpoint -}}
{{- end -}}