<#macro PreAuthorize roles>
  <#list roles>@org.springframework.security.access.prepost.PreAuthorize("<#items as role>${role}<#sep> && </#items>")</#list>
</#macro>
package ${basePackage}.restapi.controller;

import com.epam.digital.data.platform.model.core.kafka.Request;
import com.epam.digital.data.platform.model.core.kafka.RequestContext;
import com.epam.digital.data.platform.model.core.kafka.SecurityContext;
import com.epam.digital.data.platform.restapi.core.annotation.HttpRequestContext;
import com.epam.digital.data.platform.restapi.core.annotation.HttpSecurityContext;
import java.util.List;
import com.epam.digital.data.platform.restapi.core.utils.ResponseResolverUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ${basePackage}.model.dto.${schemaName};
import ${basePackage}.model.dto.${schemaName}SearchConditions;
import ${basePackage}.restapi.service.${schemaName}SearchService;

@RestController
@RequestMapping("${endpoint}")
public class ${className} {

  private final ${schemaName}SearchService searchService;

  public ${className}(
    ${schemaName}SearchService searchService) {
      this.searchService = searchService;
  }

<@PreAuthorize roles=readRoles />
  @GetMapping
  public ResponseEntity<List<${schemaName}>> search(
      ${schemaName}SearchConditions searchConditions,
      @HttpRequestContext RequestContext context,
      @HttpSecurityContext SecurityContext securityContext) {
    var request = new Request<>(searchConditions, context, securityContext);
    var response = searchService.request(request);
    return ResponseResolverUtil.getHttpResponseFromKafka(response);
  }
}
