package ${basePackage}.kafkaapi.listener;

import com.epam.digital.data.platform.kafkaapi.core.audit.AuditableListener;
import com.epam.digital.data.platform.kafkaapi.core.util.Operation;
import com.epam.digital.data.platform.kafkaapi.core.listener.GenericCreateCommandListener;
import com.epam.digital.data.platform.model.core.kafka.Request;
import com.epam.digital.data.platform.model.core.kafka.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;
import ${basePackage}.kafkaapi.commandhandler.impl.${handlerName};
import ${basePackage}.model.dto.${schemaName};

import static com.epam.digital.data.platform.kafkaapi.core.util.Header.DIGITAL_SEAL;

@Component
public class ${className} extends
    GenericCreateCommandListener<${schemaName}, ${outputType}> {

  private final Logger log = LoggerFactory.getLogger(${className}.class);

  public ${className}(${handlerName} commandHandler) {
    super(commandHandler);
  }

  @AuditableListener(Operation.CREATE)
  @Override
  @KafkaListener(topics = "\u0023{kafkaProperties.topics['${operation}-${rootOfTopicName}']}",
    groupId = "\u0023{kafkaProperties.consumer.groupId}",
    containerFactory = "concurrentKafkaListenerContainerFactory")
  @SendTo
  public Message<Response<${outputType}>> create(@Header(name = DIGITAL_SEAL, required = false) String key, Request<${schemaName}> input) {
    log.info("Kafka event received with create");
    var response = super.create(key, input);
    log.info("create kafka event processing finished");
    return response;
  }
}
