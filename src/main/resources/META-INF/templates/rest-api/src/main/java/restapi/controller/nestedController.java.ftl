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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ${basePackage}.model.dto.${schemaName};
import ${basePackage}.restapi.service.${schemaName}UpsertService;

@RestController
@RequestMapping("/nested")
public class ${className} {

  private final Logger log = LoggerFactory.getLogger(${className}.class);

  private final ${schemaName}UpsertService upsertService;

  public ${className}(${schemaName}UpsertService upsertService) {
      this.upsertService = upsertService;
  }

  @AuditableController
  @PutMapping("${endpoint}")
  public ResponseEntity<EntityId> upsert${schemaName}(
    @Valid @RequestBody ${schemaName} entity,
    @HttpRequestContext RequestContext context,
    @HttpSecurityContext SecurityContext securityContext) {
    log.info("PUT /nested${endpoint} called");
    var request = new Request<>(entity, context, securityContext);
    var response = upsertService.request(request);
    return ResponseResolverUtil.getHttpResponseFromKafka(response);
  }
}
