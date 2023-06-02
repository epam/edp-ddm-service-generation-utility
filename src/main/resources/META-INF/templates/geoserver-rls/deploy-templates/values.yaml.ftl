name: ${register}-geoserver-rls

<#if geoRls?has_content>
geoRlsParams:
<#list geoRls as geoRlsEntry>
  - jwtAttribute: ${geoRlsEntry.rls.jwtAttribute}
    checkTable: ${geoRlsEntry.rls.checkTable}
    checkColumn: ${geoRlsEntry.rls.checkColumn}
    bbox: ${geoRlsEntry.geometryColumn}
</#list>
</#if>

keycloak:
  officerClient:
    clientName: realm-admin
    realm: officer-portal
  certificatesEndpoint: /protocol/openid-connect/certs