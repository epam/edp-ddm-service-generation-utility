package ${basePackage}.kafkaapi.commandhandler.impl;

import org.springframework.stereotype.Service;
import com.epam.digital.data.platform.kafkaapi.core.commandhandler.AbstractCommandHandler;
import com.epam.digital.data.platform.kafkaapi.core.commandhandler.util.EntityConverter;
import ${basePackage}.model.dto.${schemaName};

@Service
public class ${className} extends
    AbstractCommandHandler<${schemaName}> {

  public ${className}(
      EntityConverter<${schemaName}> entityConverter) {
    super(entityConverter);
  }

  @Override
  public String tableName() {
    return "${tableName}";
  }

  @Override
  public String pkColumnName() {
    return "${pkColumnName}";
  }

}
