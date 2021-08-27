<#macro PreAuthorize roles>
  <#list roles>@org.springframework.security.access.prepost.PreAuthorize("<#items as role>${role}<#sep> && </#items>")</#list>
</#macro>
package ${basePackage}.restapi.controller;

import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.epam.digital.data.platform.model.core.kafka.Request;
import com.epam.digital.data.platform.model.core.kafka.RequestContext;
import com.epam.digital.data.platform.model.core.kafka.SecurityContext;
import com.epam.digital.data.platform.restapi.core.annotation.HttpRequestContext;
import com.epam.digital.data.platform.restapi.core.annotation.HttpSecurityContext;
import com.epam.digital.data.platform.restapi.core.utils.ResponseResolverUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

import ${basePackage}.model.dto.${schemaName};
import ${basePackage}.restapi.service.${schemaName}UpdateService;

@RestController
@RequestMapping("${endpoint}")
public class ${className} {

  private final Logger log = LoggerFactory.getLogger(${className}.class);

  private final ${schemaName}UpdateService updateService;

  public ${className}(${schemaName}UpdateService updateService) {
      this.updateService = updateService;
  }

<@PreAuthorize roles=updateRoles />
  @PatchMapping("/{id}")
  public ResponseEntity<Void> update${schemaName}(
    @PathVariable("id") ${pkType} id,
    @Valid @RequestBody ${schemaName} ${schemaName?uncap_first},
    @HttpRequestContext RequestContext context,
    @HttpSecurityContext SecurityContext securityContext) {
    log.info("PATCH ${endpoint}/{id} called");
    ${schemaName?uncap_first}.set${pkName?cap_first}(id);
    Request<${schemaName}> request = new Request<>(${schemaName?uncap_first}, context, securityContext);
    var response = updateService.request(request);
    return ResponseResolverUtil.getHttpResponseFromKafka(response);
  }
}
