<#macro PreAuthorize roles>
  <#list roles>@org.springframework.security.access.prepost.PreAuthorize("<#items as role>${role}<#sep> && </#items>")</#list>
</#macro>
package ${basePackage}.restapi.controller;

import javax.validation.Valid;
import com.epam.digital.data.platform.model.core.kafka.Request;
import com.epam.digital.data.platform.model.core.kafka.RequestContext;
import com.epam.digital.data.platform.model.core.kafka.SecurityContext;
import com.epam.digital.data.platform.restapi.core.annotation.HttpRequestContext;
import com.epam.digital.data.platform.restapi.core.annotation.HttpSecurityContext;
import com.epam.digital.data.platform.restapi.core.audit.AuditableController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.epam.digital.data.platform.restapi.core.utils.ResponseResolverUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ${basePackage}.model.dto.${schemaName}SearchConditionResponse;
import ${basePackage}.model.dto.${schemaName}SearchConditions;
import ${basePackage}.restapi.service.${serviceName};

@RestController
public class ${className} {

  private final Logger log = LoggerFactory.getLogger(${className}.class);

  private final ${serviceName} searchService;

  public ${className}(
        ${serviceName} searchService) {
      this.searchService = searchService;
  }

  @AuditableController
<@PreAuthorize roles=readRoles />
  @GetMapping("${endpoint}")
  public ResponseEntity<${responseType}<${schemaName}SearchConditionResponse>> search(
      @Valid ${schemaName}SearchConditions searchConditions,
      @HttpRequestContext RequestContext context,
      @HttpSecurityContext SecurityContext securityContext) {
    log.info("GET ${endpoint} called");
    var request = new Request<>(searchConditions, context, securityContext);
    var response = searchService.request(request);
    return ResponseResolverUtil.getHttpResponseFromKafka(response);
  }

  @AuditableController
<@PreAuthorize roles=readRoles />
  @GetMapping("/search${endpoint}")
  public ResponseEntity<${responseType}<${schemaName}SearchConditionResponse>> searchGet(
      @Valid ${schemaName}SearchConditions searchConditions,
      @HttpRequestContext RequestContext context,
      @HttpSecurityContext SecurityContext securityContext) {
    log.info("GET /search${endpoint} called");
    var request = new Request<>(searchConditions, context, securityContext);
    var response = searchService.request(request);
    return ResponseResolverUtil.getHttpResponseFromKafka(response);
  }

  @AuditableController
<@PreAuthorize roles=readRoles />
  @PostMapping("/search${endpoint}")
  public ResponseEntity<${responseType}<${schemaName}SearchConditionResponse>> searchPost(
      @Valid @RequestBody ${schemaName}SearchConditions searchConditions,
      @HttpRequestContext RequestContext context,
      @HttpSecurityContext SecurityContext securityContext) {
    log.info("POST /search${endpoint} called");
    var request = new Request<>(searchConditions, context, securityContext);
    var response = searchService.request(request);
    return ResponseResolverUtil.getHttpResponseFromKafka(response);
  }
}
