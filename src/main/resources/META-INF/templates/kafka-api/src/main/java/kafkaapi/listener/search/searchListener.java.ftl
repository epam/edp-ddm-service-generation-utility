package ${basePackage}.kafkaapi.listener.search;

import com.epam.digital.data.platform.kafkaapi.core.annotation.KafkaAudit;
import com.epam.digital.data.platform.kafkaapi.core.util.Operation;
import com.epam.digital.data.platform.kafkaapi.core.listener.GenericSearchListener;
import com.epam.digital.data.platform.model.core.kafka.Request;
import com.epam.digital.data.platform.model.core.kafka.Response;
import java.util.List;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;
import ${basePackage}.kafkaapi.searchhandler.impl.${schemaName}SearchHandler;
import ${basePackage}.model.dto.${schemaName};
import ${basePackage}.model.dto.${schemaName}SearchConditions;

@Component
public class ${schemaName}Listener extends
    GenericSearchListener<${schemaName}SearchConditions, ${schemaName}> {

  public ${schemaName}Listener(
      ${schemaName}SearchHandler searchHandler) {
    super(searchHandler);
  }

  @KafkaAudit(Operation.SEARCH)
  @Override
  @KafkaListener(topics = "\u0023{kafkaProperties.topics['search-${listeners[0].rootOfTopicName}']}",
      groupId = "\u0023{kafkaProperties.groupId}",
      containerFactory = "concurrentKafkaListenerContainerFactory")
  @SendTo
  public Message<Response<List<${schemaName}>>> search(
      @Header(name = DIGITAL_SEAL, required = false) String key,
      Request<${schemaName}SearchConditions> searchConditions) {
    return super.search(key, searchConditions);
  }
}
