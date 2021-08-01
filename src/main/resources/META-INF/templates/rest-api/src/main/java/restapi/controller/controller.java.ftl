<#macro PreAuthorize roles>
  <#list roles>@org.springframework.security.access.prepost.PreAuthorize("<#items as role>${role}<#sep> && </#items>")</#list>
</#macro>
package ${basePackage}.restapi.controller;

import javax.validation.Valid;
import com.epam.digital.data.platform.model.core.kafka.EntityId;
import com.epam.digital.data.platform.model.core.kafka.Request;
import com.epam.digital.data.platform.model.core.kafka.RequestContext;
import com.epam.digital.data.platform.model.core.kafka.SecurityContext;
import com.epam.digital.data.platform.restapi.core.annotation.HttpRequestContext;
import com.epam.digital.data.platform.restapi.core.annotation.HttpSecurityContext;
import com.epam.digital.data.platform.restapi.core.utils.ResponseResolverUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ${basePackage}.model.dto.${schemaName};
import ${basePackage}.restapi.service.${schemaName}CreateService;
import ${basePackage}.restapi.service.${schemaName}DeleteService;
import ${basePackage}.restapi.service.${schemaName}ReadService;
import ${basePackage}.restapi.service.${schemaName}UpdateService;

@RestController
@RequestMapping("${endpoint}")
public class ${className} {

  private final ${schemaName}CreateService createService;
  private final ${schemaName}ReadService readService;
  private final ${schemaName}UpdateService updateService;
  private final ${schemaName}DeleteService deleteService;

  public ${className}(
    ${schemaName}CreateService createService,
    ${schemaName}ReadService readService,
    ${schemaName}UpdateService updateService,
    ${schemaName}DeleteService deleteService) {
      this.createService = createService;
      this.readService = readService;
      this.updateService = updateService;
      this.deleteService = deleteService;
  }

<@PreAuthorize roles=readRoles />
  @GetMapping("/{id}")
  public ResponseEntity<${schemaName}> findById${schemaName}(
      @PathVariable("id") ${pkType} id,
      @HttpRequestContext RequestContext context,
      @HttpSecurityContext SecurityContext securityContext) {
    Request<${pkType}> request = new Request<>(id, context, securityContext);
    var response = readService.request(request);
    return ResponseResolverUtil.getHttpResponseFromKafka(response);
  }

<@PreAuthorize roles=createRoles />
  @PostMapping
  public ResponseEntity<EntityId> create${schemaName}(
    @Valid @RequestBody ${schemaName} ${schemaName?uncap_first},
    @HttpRequestContext RequestContext context,
    @HttpSecurityContext SecurityContext securityContext) {
    Request<${schemaName}> request = new Request<>(${schemaName?uncap_first}, context, securityContext);
    var response = createService.request(request);
    return ResponseResolverUtil.getHttpResponseFromKafka(response);
  }

<@PreAuthorize roles=updateRoles />
  @PutMapping("/{id}")
  public ResponseEntity<Void> update${schemaName}(
    @PathVariable("id") ${pkType} id,
    @Valid @RequestBody ${schemaName} ${schemaName?uncap_first},
    @HttpRequestContext RequestContext context,
    @HttpSecurityContext SecurityContext securityContext) {
    ${schemaName?uncap_first}.set${pkName?cap_first}(id);
    Request<${schemaName}> request = new Request<>(${schemaName?uncap_first}, context, securityContext);
    var response = updateService.request(request);
    return ResponseResolverUtil.getHttpResponseFromKafka(response);
  }

<@PreAuthorize roles=deleteRoles />
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteById${schemaName}(@PathVariable("id") ${pkType} id,
      @HttpRequestContext RequestContext context,
      @HttpSecurityContext SecurityContext securityContext) {
    ${schemaName} ${schemaName?uncap_first} = new ${schemaName}();
    ${schemaName?uncap_first}.set${pkName?cap_first}(id);
    Request<${schemaName}> request = new Request<>(${schemaName?uncap_first}, context, securityContext);
    var response = deleteService.request(request);
    return ResponseResolverUtil.getHttpResponseFromKafka(response);
  }
}
