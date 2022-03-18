package ${basePackage}.kafkaapi.listener.query;

import com.epam.digital.data.platform.kafkaapi.core.audit.AuditableListener;
import com.epam.digital.data.platform.kafkaapi.core.util.Operation;
import com.epam.digital.data.platform.kafkaapi.core.listener.GenericQueryListener;
import com.epam.digital.data.platform.model.core.kafka.Request;
import com.epam.digital.data.platform.model.core.kafka.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;
import ${basePackage}.kafkaapi.queryhandler.${schemaName}QueryHandler;
import ${basePackage}.model.dto.${schemaName};

@Component
public class ${className} extends
    GenericQueryListener<${pkType}, ${schemaName}> {

  private final Logger log = LoggerFactory.getLogger(${className}.class);

  public ${className}(
    ${schemaName}QueryHandler queryHandler) {
        super(queryHandler);
    }

  @AuditableListener(Operation.READ)
  @Override
  @KafkaListener(topics = "\u0023{kafkaProperties.topics['read-${rootOfTopicName}']}",
    groupId = "\u0023{kafkaProperties.consumer.groupId}",
    containerFactory = "concurrentKafkaListenerContainerFactory")
  @SendTo
  public Message<Response<${schemaName}>> read(@Header(name = DIGITAL_SEAL, required = false) String key, Request<${pkType}> input) {
    log.info("Kafka event received with read");
    var response = super.read(key, input);
    log.info("read kafka event processing finished");
    return response;
  }
}
