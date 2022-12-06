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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ${basePackage}.model.dto.${schemaName}Model;
import ${basePackage}.restapi.service.${schemaName}CreateService;
import ${basePackage}.restapi.service.${schemaName}DeleteService;
import ${basePackage}.restapi.service.${schemaName}UpdateService;

@RestController
@RequestMapping("${endpoint}")
public class ${className} {

  private final Logger log = LoggerFactory.getLogger(${className}.class);

  private final ${schemaName}CreateService createService;
  private final ${schemaName}UpdateService updateService;
  private final ${schemaName}DeleteService deleteService;

  public ${className}(
    ${schemaName}CreateService createService,
    ${schemaName}UpdateService updateService,
    ${schemaName}DeleteService deleteService) {
      this.createService = createService;
      this.updateService = updateService;
      this.deleteService = deleteService;
  }

  @AuditableController
<@PreAuthorize roles=createRoles />
  @PostMapping
  public ResponseEntity<EntityId> create${schemaName}(
    @Valid @RequestBody ${schemaName}Model entity,
    @HttpRequestContext RequestContext context,
    @HttpSecurityContext SecurityContext securityContext) {
    log.info("POST ${endpoint} called");
    var request = new Request<>(entity, context, securityContext);
    var response = createService.request(request);
    return ResponseResolverUtil.getHttpResponseFromKafka(response);
  }

  @AuditableController
<@PreAuthorize roles=updateRoles />
  @PutMapping("/{id}")
  public ResponseEntity<Void> update${schemaName}(
    @PathVariable("id") ${pkType} id,
    @Valid @RequestBody ${schemaName}Model entity,
    @HttpRequestContext RequestContext context,
    @HttpSecurityContext SecurityContext securityContext) {
    log.info("PUT ${endpoint}/{id} called");
    entity.set${pkName?cap_first}(id);
    var request = new Request<>(entity, context, securityContext);
    var response = updateService.request(request);
    return ResponseResolverUtil.getHttpResponseFromKafka(response);
  }

  @AuditableController
<@PreAuthorize roles=deleteRoles />
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteById${schemaName}(@PathVariable("id") ${pkType} id,
      @HttpRequestContext RequestContext context,
      @HttpSecurityContext SecurityContext securityContext) {
    log.info("DELETE ${endpoint}/{id} called");
    var entity = new ${schemaName}Model();
    entity.set${pkName?cap_first}(id);
    var request = new Request<>(entity, context, securityContext);
    var response = deleteService.request(request);
    return ResponseResolverUtil.getHttpResponseFromKafka(response);
  }
}
