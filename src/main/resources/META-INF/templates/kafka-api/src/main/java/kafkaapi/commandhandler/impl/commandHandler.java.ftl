package ${basePackage}.kafkaapi.commandhandler.impl;

import com.epam.digital.data.platform.kafkaapi.core.commandhandler.Abstract${operation?capitalize}CommandHandler;
import com.epam.digital.data.platform.kafkaapi.core.commandhandler.util.EntityConverter;
import org.springframework.stereotype.Service;
import ${basePackage}.kafkaapi.tabledata.${tableDataProviderName};
import ${basePackage}.model.dto.${schemaName};

@Service
public class ${className} extends Abstract${operation?capitalize}CommandHandler<${schemaName}> {

  public ${className}(
      EntityConverter<${schemaName}> entityConverter,
      ${tableDataProviderName} tableDataProvider) {
    super(entityConverter, tableDataProvider);
  }
}
