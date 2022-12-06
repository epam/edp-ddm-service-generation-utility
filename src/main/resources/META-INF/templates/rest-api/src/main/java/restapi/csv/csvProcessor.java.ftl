package ${basePackage}.restapi.csv;

import com.epam.digital.data.platform.restapi.core.service.AbstractCsvProcessor;
import org.springframework.stereotype.Service;
import ${basePackage}.model.dto.${csvRowSchemaName};
import ${basePackage}.model.dto.${csvPayloadSchemaName};

import java.util.List;

@Service
public class ${className}
        extends AbstractCsvProcessor<${csvRowSchemaName}, ${csvPayloadSchemaName}> {

  @Override
  protected Class<${csvRowSchemaName}> getCsvRowElementType() {
    return ${csvRowSchemaName}.class;
  }

  @Override
  protected ${csvPayloadSchemaName} getPayloadObjectFromCsvRows(
          List<${csvRowSchemaName}> list) {
    var payload = new ${csvPayloadSchemaName}();
    payload.setEntities(list.toArray(${csvRowSchemaName}[]::new));
    return payload;
  }
}