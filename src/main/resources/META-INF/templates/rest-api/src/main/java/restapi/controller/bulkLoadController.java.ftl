<#macro PreAuthorize roles>
  <#list roles>@org.springframework.security.access.prepost.PreAuthorize("<#items as role>${role}<#sep> && </#items>")</#list>
</#macro>
package ${basePackage}.restapi.controller;

import java.util.List;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.epam.digital.data.platform.model.core.kafka.EntityId;
import com.epam.digital.data.platform.model.core.kafka.File;
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

import ${basePackage}.model.dto.${schemaName}CreateList;
import ${basePackage}.restapi.csv.${schemaName}CsvProcessor;
import ${basePackage}.restapi.service.${schemaName}CreateListService;
import ${basePackage}.restapi.service.${schemaName}CreateCsvService;

@RestController
@RequestMapping("${endpoint}")
public class ${className} {

  private final Logger log = LoggerFactory.getLogger(${className}.class);

  private final ${schemaName}CreateListService createListService;
  private final ${schemaName}CreateCsvService createCsvService;
  private final ${schemaName}CsvProcessor csvProcessor;

  public ${className}(
    ${schemaName}CreateListService createListService,
    ${schemaName}CreateCsvService createCsvService,
    ${schemaName}CsvProcessor csvProcessor) {
      this.createListService = createListService;
      this.createCsvService = createCsvService;
      this.csvProcessor = csvProcessor;
  }

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

  @AuditableController
<@PreAuthorize roles=createRoles />
  @PostMapping("/csv")
  public ResponseEntity<List<EntityId>> create${schemaName}Csv(
    @Valid @RequestBody File file,
    @HttpRequestContext RequestContext context,
    @HttpSecurityContext SecurityContext securityContext) {
    log.info("POST ${endpoint}/csv called");
    Request<File> request = new Request<>(file, context, securityContext);
    var response = createCsvService.request(request);
    return ResponseResolverUtil.getHttpResponseFromKafka(response);
  }

  @PostMapping("/csv/validation")
  public ResponseEntity<Void> validate${schemaName}Csv(
    @Valid @RequestBody File file,
    @HttpRequestContext RequestContext context,
    @HttpSecurityContext SecurityContext securityContext) {
    log.info("POST ${endpoint}/csv/validation called");
    Request<File> request = new Request<>(file, context, securityContext);
    csvProcessor.validate(request);
    return ResponseEntity.ok().build();
  }
}
