package ${basePackage}.restapi.service;

import com.epam.digital.data.platform.restapi.core.service.GenericQueryService;
import org.springframework.stereotype.Service;
import ${basePackage}.model.dto.${schemaName};
import ${basePackage}.restapi.handler.${schemaName}QueryHandler;

@Service
public class ${className}
    extends GenericQueryService<${pkType}, ${schemaName}> {

  public ${className}(
      ${schemaName}QueryHandler handler) {
    super(handler);
  }
}
