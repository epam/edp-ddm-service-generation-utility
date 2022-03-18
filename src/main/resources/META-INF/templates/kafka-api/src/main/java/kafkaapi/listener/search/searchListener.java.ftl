package ${basePackage}.kafkaapi.listener.search;

import com.epam.digital.data.platform.kafkaapi.core.audit.AuditableListener;
import com.epam.digital.data.platform.kafkaapi.core.util.Operation;
import com.epam.digital.data.platform.kafkaapi.core.listener.GenericSearchListener;
import com.epam.digital.data.platform.model.core.kafka.Request;
import com.epam.digital.data.platform.model.core.kafka.Response;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;
import ${basePackage}.kafkaapi.searchhandler.${schemaName}SearchHandler;
import ${basePackage}.model.dto.${schemaName};
import ${basePackage}.model.dto.${schemaName}SearchConditions;

import static com.epam.digital.data.platform.kafkaapi.core.util.Header.DIGITAL_SEAL;

@Component
public class ${schemaName}Listener extends
    GenericSearchListener<${schemaName}SearchConditions, ${schemaName}> {

  private final Logger log = LoggerFactory.getLogger(${className}.class);

  public ${schemaName}Listener(
      ${schemaName}SearchHandler searchHandler) {
    super(searchHandler);
  }

  @AuditableListener(Operation.SEARCH)
  @Override
  @KafkaListener(topics = "\u0023{kafkaProperties.topics['search-${rootOfTopicName}']}",
      groupId = "\u0023{kafkaProperties.consumer.groupId}",
      containerFactory = "concurrentKafkaListenerContainerFactory")
  @SendTo
  public Message<Response<List<${schemaName}>>> search(
      @Header(name = DIGITAL_SEAL, required = false) String key,
      Request<${schemaName}SearchConditions> searchConditions) {
    log.info("Kafka event received with search");
    var response = super.search(key, searchConditions);
    log.info("search kafka event processing finished");
    return response;
  }
}
