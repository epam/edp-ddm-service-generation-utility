package ${basePackage}.restapi.service;

import com.epam.digital.data.platform.restapi.core.service.GenericSearchService;
import org.springframework.stereotype.Service;
import ${basePackage}.model.dto.${schemaName};
import ${basePackage}.model.dto.${schemaName}SearchConditions;
import ${basePackage}.restapi.handler.${schemaName}SearchHandler;

@Service
public class ${className}
    extends GenericSearchService<
            ${schemaName}SearchConditions,
            ${schemaName}> {


  public ${schemaName}SearchService(
      ${schemaName}SearchHandler handler) {
    super(handler);
  }

}
