package ${basePackage}.restapi.service;

import com.epam.digital.data.platform.restapi.core.service.GenericQueryService;
import org.springframework.stereotype.Service;
import ${basePackage}.model.dto.${schemaName};
import ${basePackage}.restapi.handler.${handlerName};

@Service
public class ${className}
    extends GenericQueryService<${pkType}, ${schemaName}> {

  public ${className}(
      ${handlerName} handler) {
    super(handler);
  }
}
