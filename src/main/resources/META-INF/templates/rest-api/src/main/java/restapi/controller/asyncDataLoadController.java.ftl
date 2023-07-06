package ${basePackage}.restapi.controller;

import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.epam.digital.data.platform.model.core.kafka.File;
import com.epam.digital.data.platform.model.core.kafka.Request;
import com.epam.digital.data.platform.model.core.kafka.RequestContext;
import com.epam.digital.data.platform.model.core.kafka.SecurityContext;
import com.epam.digital.data.platform.restapi.core.annotation.HttpRequestContext;
import com.epam.digital.data.platform.restapi.core.annotation.HttpSecurityContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ${basePackage}.restapi.csv.${schemaName}AsyncDataLoadCsvProcessor;

@RestController
@RequestMapping("${endpoint}")
public class ${className} {

  private final Logger log = LoggerFactory.getLogger(${className}.class);

  private final ${schemaName}AsyncDataLoadCsvProcessor csvProcessor;

  public ${className}(${schemaName}AsyncDataLoadCsvProcessor csvProcessor) {
      this.csvProcessor = csvProcessor;
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
