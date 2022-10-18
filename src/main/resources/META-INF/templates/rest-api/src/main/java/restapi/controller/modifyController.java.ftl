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

import ${basePackage}.model.dto.${schemaName};
import ${basePackage}.restapi.service.${schemaName}CreateService;
import ${basePackage}.restapi.service.${schemaName}DeleteService;
import ${basePackage}.restapi.service.${schemaName}UpdateService;

<#if bulkLoadEnabled>
import java.util.List;
import ${basePackage}.model.dto.${schemaName}CreateList;
import ${basePackage}.restapi.service.${schemaName}CreateListService;
</#if>

@RestController
@RequestMapping("${endpoint}")
public class ${className} {

  private final Logger log = LoggerFactory.getLogger(${className}.class);

  private final ${schemaName}CreateService createService;
  <#if bulkLoadEnabled>
  private final ${schemaName}CreateListService createListService;
  </#if>
  private final ${schemaName}UpdateService updateService;
  private final ${schemaName}DeleteService deleteService;

  public ${className}(
    ${schemaName}CreateService createService,
    <#if bulkLoadEnabled>
    ${schemaName}CreateListService createListService,
    </#if>
    ${schemaName}UpdateService updateService,
    ${schemaName}DeleteService deleteService) {
      this.createService = createService;
      <#if bulkLoadEnabled>
      this.createListService = createListService;
      </#if>
      this.updateService = updateService;
      this.deleteService = deleteService;
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

  <#if bulkLoadEnabled>
  @AuditableController
<@PreAuthorize roles=createRoles />
  @PostMapping("/list")
  public ResponseEntity<List<EntityId>> create${schemaName}List(
    @Valid @RequestBody ${schemaName}CreateList ${schemaName?uncap_first}CreateList,
    @HttpRequestContext RequestContext context,
    @HttpSecurityContext SecurityContext securityContext) {
    log.info("POST ${endpoint}/list called");
    Request<${schemaName}CreateList> request = new Request<>(${schemaName?uncap_first}CreateList, context, securityContext);
    var response = createListService.request(request);
    return ResponseResolverUtil.getHttpResponseFromKafka(response);
  }
  </#if>

  @AuditableController
<@PreAuthorize roles=updateRoles />
  @PutMapping("/{id}")
  public ResponseEntity<Void> update${schemaName}(
    @PathVariable("id") ${pkType} id,
    @Valid @RequestBody ${schemaName} ${schemaName?uncap_first},
    @HttpRequestContext RequestContext context,
    @HttpSecurityContext SecurityContext securityContext) {
    log.info("PUT ${endpoint}/{id} called");
    ${schemaName?uncap_first}.set${pkName?cap_first}(id);
    Request<${schemaName}> request = new Request<>(${schemaName?uncap_first}, context, securityContext);
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
    ${schemaName} ${schemaName?uncap_first} = new ${schemaName}();
    ${schemaName?uncap_first}.set${pkName?cap_first}(id);
    Request<${schemaName}> request = new Request<>(${schemaName?uncap_first}, context, securityContext);
    var response = deleteService.request(request);
    return ResponseResolverUtil.getHttpResponseFromKafka(response);
  }
}
