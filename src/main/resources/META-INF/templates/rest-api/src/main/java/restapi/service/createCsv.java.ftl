package ${basePackage}.restapi.service;

import com.epam.digital.data.platform.model.core.kafka.EntityId;
import com.epam.digital.data.platform.model.core.kafka.File;
import com.epam.digital.data.platform.model.core.kafka.Request;
import com.epam.digital.data.platform.model.core.kafka.Response;
import com.epam.digital.data.platform.restapi.core.service.KafkaService;
import org.springframework.stereotype.Service;
import ${basePackage}.restapi.csv.${schemaName}CsvProcessor;

import java.util.List;

@Service
public class ${className} implements KafkaService<File, List<EntityId>> {

  private final ${schemaName}CreateListService service;
  private final ${schemaName}CsvProcessor csvProcessor;

  public ${className}(
        ${schemaName}CreateListService service,
        ${schemaName}CsvProcessor csvProcessor) {
    this.service = service;
    this.csvProcessor = csvProcessor;
  }

  @Override
  public Response<List<EntityId>> request(Request<File> input) {
    var payload = csvProcessor.transformFileToEntity(input);
    var request = new Request<>(payload, input.getRequestContext(), input.getSecurityContext());
    return service.request(request);
  }
}