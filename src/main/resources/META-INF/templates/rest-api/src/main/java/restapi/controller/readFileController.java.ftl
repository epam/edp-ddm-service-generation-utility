<#macro PreAuthorize roles>
  <#list roles>@org.springframework.security.access.prepost.PreAuthorize("<#items as role>${role}<#sep> && </#items>")</#list>
</#macro>
package ${basePackage}.restapi.controller;

import com.epam.digital.data.platform.model.core.file.FileResponseDto;
import com.epam.digital.data.platform.model.core.kafka.Request;
import com.epam.digital.data.platform.model.core.kafka.RequestContext;
import com.epam.digital.data.platform.model.core.kafka.SecurityContext;
import com.epam.digital.data.platform.restapi.core.annotation.HttpRequestContext;
import com.epam.digital.data.platform.restapi.core.annotation.HttpSecurityContext;
import com.epam.digital.data.platform.restapi.core.audit.AuditableController;
import com.epam.digital.data.platform.restapi.core.model.FileRequestDto;
import com.epam.digital.data.platform.restapi.core.utils.ResponseResolverUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ${basePackage}.restapi.service.${serviceName};

import static com.epam.digital.data.platform.restapi.core.utils.FileUtils.ATTACHMENT_HEADER_VALUE;

@RestController
@RequestMapping("${endpoint}")
public class ${className} {

  private final Logger log = LoggerFactory.getLogger(${className}.class);

  private final ${serviceName} readFileService;

  public ${className}(
        ${serviceName} readFileService) {
      this.readFileService = readFileService;
  }

  @AuditableController
<@PreAuthorize roles=readRoles />
  @GetMapping(value = "/{id}/${columnEndpoint}/{fileId}", produces = { MediaType.APPLICATION_JSON_VALUE })
  public ResponseEntity<FileResponseDto> findFileDto(
      @PathVariable("id") ${pkType} id,
      @PathVariable("fileId") String fileId,
      @HttpRequestContext RequestContext context,
      @HttpSecurityContext SecurityContext securityContext) {
    log.info("GET ${endpoint}/{id}/${columnEndpoint}/{fileId} for json called");
    var fileRequestDto = new FileRequestDto<>(id, fileId);
    var request = new Request<>(fileRequestDto, context, securityContext);
    var response = readFileService.requestDto(request);
    return ResponseResolverUtil.getHttpResponseFromKafka(response);
  }

  @AuditableController
  <@PreAuthorize roles=readRoles />
  @GetMapping(value = "/{id}/${columnEndpoint}/{fileId}", produces = { "!" + MediaType.APPLICATION_JSON_VALUE })
  public ResponseEntity<Resource> findFileResource(
      @PathVariable("id") ${pkType} id,
      @PathVariable("fileId") String fileId,
      @HttpRequestContext RequestContext context,
      @HttpSecurityContext SecurityContext securityContext) {
    log.info("GET ${endpoint}/{id}/${columnEndpoint}/{fileId} for file called");
    var fileRequestDto = new FileRequestDto<>(id, fileId);
    var request = new Request<>(fileRequestDto, context, securityContext);
    var responsePayload = readFileService.requestFile(request).getPayload();
    return ResponseEntity.ok()
        .contentLength(responsePayload.getMetadata().getContentLength())
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .header(HttpHeaders.CONTENT_DISPOSITION,
            String.format(ATTACHMENT_HEADER_VALUE, responsePayload.getMetadata().getFilename()))
        .body(new InputStreamResource(responsePayload.getContent()));
    }
}
