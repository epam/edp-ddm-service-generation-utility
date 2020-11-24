package ${basePackage}.restapi.tabledata;

import com.epam.digital.data.platform.restapi.core.tabledata.TableDataProvider;
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