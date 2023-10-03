package ${basePackage}.restapi.service;

import com.epam.digital.data.platform.model.core.kafka.File;
import com.epam.digital.data.platform.restapi.core.service.GenericFileQueryService;
import org.springframework.stereotype.Service;

import ${basePackage}.model.dto.${schemaName};
import ${basePackage}.restapi.handler.${handlerName};

import java.util.Optional;

@Service
public class ${className} extends GenericFileQueryService<${pkType}, ${schemaName}> {

  public ${className}(${handlerName} handler) {
    super(handler);
  }

  @Override
  public Optional<File> getFileFieldById(
      ${schemaName} entity, String requestedFileId) {
    <#if fieldTypeList>
    return Optional.ofNullable(entity.get${fileFieldName?cap_first}())
        .orElse(java.util.Collections.emptyList()).stream()
        .filter(java.util.Objects::nonNull)
        .filter(file -> requestedFileId.equals(file.getId()))
        .findFirst();
    <#else>
    return Optional.ofNullable(entity.get${fileFieldName?cap_first}())
        .filter(file -> requestedFileId.equals(file.getId()));
    </#if>
  }
}
