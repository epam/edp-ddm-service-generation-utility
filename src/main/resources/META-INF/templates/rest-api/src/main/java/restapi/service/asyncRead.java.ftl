package ${basePackage}.restapi.service;

import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.stereotype.Service;
import ${basePackage}.model.dto.${schemaName};
import com.epam.digital.data.platform.model.core.kafka.Request;
import com.epam.digital.data.platform.model.core.kafka.Response;
import com.epam.digital.data.platform.restapi.core.service.GenericService;
import com.epam.digital.data.platform.starter.kafka.config.properties.KafkaProperties;
import com.fasterxml.jackson.core.type.TypeReference;

@Service
public class ${className} extends GenericService<Request<${pkType}>, ${schemaName}> {

  private static final String REQUEST_TYPE = "${requestType}";

  public ${className}(
    ReplyingKafkaTemplate<String, Request<${pkType}>, String> replyingKafkaTemplate,
      KafkaProperties kafkaProperties) {

    super(replyingKafkaTemplate, kafkaProperties.getRequestReply().getTopics().get(REQUEST_TYPE));
  }

  @Override
  protected TypeReference type() {
    return new TypeReference<Response<${schemaName}>>() {};
  }
}
