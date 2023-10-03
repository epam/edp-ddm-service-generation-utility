package ${basePackage}.soapapi.restclients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.epam.digital.data.platform.model.core.file.FileResponseDto;
import com.epam.digital.data.platform.soapapi.core.config.feign.FeignConfig;
import java.util.Map;

<#list schemaNames as schema>
import ${basePackage}.model.dto.${schema};
</#list>

<#noparse>
@FeignClient(name = "RestApiClient", url = "${rest-api.url}", configuration = FeignConfig.class)
public interface RestApiClient {
</#noparse>

  <#list searchScopes as scope>
  @PostMapping("/search${scope.endpoint}")
  ${scope.responseType}<${scope.schemaName}SearchConditionResponse> search${scope.schemaName}(
    @RequestBody ${scope.schemaName}SearchConditions searchConditions,
    @RequestHeader Map<String, Object> headers);
  </#list>

  <#list fileScopes as scope>
  @GetMapping(value = "/files/${scope.tableEndpoint}/{id}/${scope.columnEndpoint}/{fileId}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  FileResponseDto ${scope.methodName}(
      @PathVariable("id") ${scope.pkType} id,
      @PathVariable("fileId") String fileId,
      @RequestHeader Map<String, Object> headers);
  </#list>
}
