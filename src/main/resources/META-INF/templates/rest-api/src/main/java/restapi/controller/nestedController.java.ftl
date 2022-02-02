<#macro PreAuthorize roles>
  <#list roles>@org.springframework.security.access.prepost.PreAuthorize("<#items as role>${role}<#sep> && </#items>")</#list>
</#macro>
package ${basePackage}.restapi.controller;

import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.epam.digital.data.platform.model.core.kafka.EntityId;
import com.epam.digital.data.platform.model.core.kafka.Request;
import com.epam.digital.data.platform.model.core.kafka.RequestContext;
import com.epam.digital.data.platform.model.core.kafka.SecurityContext;
import com.epam.digital.data.platform.restapi.core.annotation.HttpRequestContext;
import com.epam.digital.data.platform.restapi.core.annotation.HttpSecurityContext;
import com.epam.digital.data.platform.restapi.core.audit.AuditableController;
import com.epam.digital.data.platform.restapi.core.utils.ResponseResolverUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ${basePackage}.model.dto.${schemaName};
import ${basePackage}.restapi.service.${schemaName}CreateService;

@RestController
@RequestMapping("${endpoint}")
public class ${className} {

  private final Logger log = LoggerFactory.getLogger(${className}.class);

  private final ${schemaName}CreateService createService;

  public ${className}(${schemaName}CreateService createService) {
      this.createService = createService;
  }

  @AuditableController
  <@PreAuthorize roles=createRoles />
  @PostMapping
  public ResponseEntity<EntityId> create${schemaName}(
    @Valid @RequestBody ${schemaName} ${schemaName?uncap_first},
    @HttpRequestContext RequestContext context,
    @HttpSecurityContext SecurityContext securityContext) {
    log.info("POST ${endpoint} called");
    Request<${schemaName}> request = new Request<>(${schemaName?uncap_first}, context, securityContext);
    var response = createService.request(request);
    return ResponseResolverUtil.getHttpResponseFromKafka(response);
  }
}
