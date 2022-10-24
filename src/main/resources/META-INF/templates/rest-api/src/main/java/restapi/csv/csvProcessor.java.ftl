package ${basePackage}.restapi.csv;

import com.epam.digital.data.platform.restapi.core.service.AbstractCsvProcessor;
import org.springframework.stereotype.Service;
import ${basePackage}.model.dto.${schemaName};
import ${basePackage}.model.dto.${schemaName}CreateList;

import java.util.List;

@Service
public class ${className}
        extends AbstractCsvProcessor<${schemaName}, ${schemaName}CreateList> {

  @Override
  protected Class<${schemaName}> getCsvRowElementType() {
    return ${schemaName}.class;
  }

  @Override
  protected ${schemaName}CreateList getPayloadObjectFromCsvRows(
          List<${schemaName}> list) {
    var payload = new ${schemaName}CreateList();
    payload.setEntities(list.toArray(${schemaName}[]::new));
    return payload;
  }
}