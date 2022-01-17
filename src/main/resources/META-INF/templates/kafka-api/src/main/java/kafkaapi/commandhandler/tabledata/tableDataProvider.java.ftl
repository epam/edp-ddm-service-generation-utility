package ${basePackage}.kafkaapi.commandhandler.tabledata;

import com.epam.digital.data.platform.kafkaapi.core.commandhandler.TableDataProvider;
import org.springframework.stereotype.Component;

@Component
public class ${className} implements TableDataProvider {

  @Override
  public String tableName() {
    return "${tableName}";
  }

  @Override
  public String pkColumnName() {
    return "${pkColumnName}";
  }
}