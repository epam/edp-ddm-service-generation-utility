package ${basePackage}.soapapi.restclients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

<#list schemaNames as schema>
import ${basePackage}.model.dto.${schema};
</#list>

import com.epam.digital.data.platform.soapapi.core.config.feign.FeignConfig;
import java.util.List;
import java.util.Map;

<#noparse>
@FeignClient(name = "RestApiClient", url = "${rest-api.url}", configuration = FeignConfig.class)
public interface RestApiClient {
</#noparse>

<#list searchScopes as scope>
  @GetMapping("${scope.endpoint}")
  List<${scope.schemaName}SearchConditionResponse> search${scope.schemaName}(
      @SpringQueryMap ${scope.schemaName}SearchConditions searchConditions,
      @RequestHeader Map<String, Object> headers);

</#list>
}
