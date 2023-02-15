package ${basePackage}.restapi.service;

import java.util.List;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.stereotype.Service;
import ${basePackage}.model.dto.${schemaName}SearchConditionResponse;
import ${basePackage}.model.dto.${schemaName}SearchConditions;
import com.epam.digital.data.platform.model.core.kafka.Request;
import com.epam.digital.data.platform.model.core.kafka.Response;
import com.epam.digital.data.platform.restapi.core.service.GenericService;
import com.epam.digital.data.platform.starter.kafka.config.properties.KafkaProperties;
import com.fasterxml.jackson.core.type.TypeReference;

@Service
public class ${schemaName}SearchService extends GenericService<${schemaName}SearchConditions, ${responseType}<${schemaName}SearchConditionResponse>> {

  private static final String REQUEST_TYPE = "${requestType}";

  public ${schemaName}SearchService(
      ReplyingKafkaTemplate<String, Request<${schemaName}SearchConditions>, String> replyingKafkaTemplate,
            KafkaProperties kafkaProperties) {
      super(replyingKafkaTemplate, kafkaProperties.getRequestReply().getTopics().get(REQUEST_TYPE));
  }

  @Override
  protected TypeReference type() {
      return new TypeReference<Response<${responseType}<${schemaName}SearchConditionResponse>>>() {};
  }

}
