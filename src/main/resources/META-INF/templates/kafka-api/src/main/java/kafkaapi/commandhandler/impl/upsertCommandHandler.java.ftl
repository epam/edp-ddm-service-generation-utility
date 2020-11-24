package ${basePackage}.kafkaapi.commandhandler.impl;

import org.springframework.stereotype.Service;

import com.epam.digital.data.platform.kafkaapi.core.commandhandler.AbstractUpsertCommandHandler;
import com.epam.digital.data.platform.kafkaapi.core.commandhandler.util.EntityConverter;
import ${basePackage}.kafkaapi.tabledata.${providerName};
import ${basePackage}.model.dto.${schemaName};

@Service
public class ${className} extends AbstractUpsertCommandHandler<${schemaName}> {

  public ${className}(
      EntityConverter<${schemaName}> entityConverter,
      ${providerName} tableDataProvider,
      ${schemaName}CreateCommandHandler createCommandHandler,
      ${schemaName}UpdateCommandHandler updateCommandHandler) {
    super(entityConverter, tableDataProvider, createCommandHandler, updateCommandHandler);
  }
}
