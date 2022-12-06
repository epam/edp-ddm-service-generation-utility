package ${basePackage}.kafkaapi.commandhandler.impl;

import org.springframework.stereotype.Service;

import com.epam.digital.data.platform.kafkaapi.core.commandhandler.AbstractUpsertCommandHandler;
import com.epam.digital.data.platform.kafkaapi.core.commandhandler.util.EntityConverter;
import ${basePackage}.kafkaapi.tabledata.${tableDataProviderName};
import ${basePackage}.model.dto.${schemaName}Model;

@Service
public class ${className} extends AbstractUpsertCommandHandler<${schemaName}Model> {

  public ${className}(
      EntityConverter<${schemaName}Model> entityConverter,
      ${tableDataProviderName} tableDataProvider,
      ${schemaName}CreateCommandHandler createCommandHandler,
      ${schemaName}UpdateCommandHandler updateCommandHandler) {
    super(entityConverter, tableDataProvider, createCommandHandler, updateCommandHandler);
  }
}
