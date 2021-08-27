package ${basePackage}.kafkaapi.listener;

import com.epam.digital.data.platform.kafkaapi.core.annotation.KafkaAudit;
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
import ${basePackage}.kafkaapi.commandhandler.impl.${schemaName}CommandHandler;
import ${basePackage}.kafkaapi.queryhandler.impl.${schemaName}QueryHandler;
import ${basePackage}.model.dto.${schemaName};

@Component
public class ${className} extends
    GenericQueryListener<${pkType}, ${schemaName}> {

  private final Logger log = LoggerFactory.getLogger(${className}.class);

  public ${className}(
    ${schemaName}CommandHandler commandHandler,
    ${schemaName}QueryHandler queryHandler) {
        super(commandHandler, queryHandler);
    }

<#list listeners as listener>
  @KafkaAudit(Operation.${listener.operation?upper_case})
  @Override
  @KafkaListener(topics = "\u0023{kafkaProperties.topics['${listener.operation}-${listener.rootOfTopicName}']}",
    groupId = "\u0023{kafkaProperties.groupId}",
    containerFactory = "concurrentKafkaListenerContainerFactory")
  @SendTo
  public Message<Response<${listener.outputType}>> ${listener.operation}(@Header(name = DIGITAL_SEAL, required = false) String key, Request<${listener.inputType}> input) {
    log.info("Kafka event received with ${listener.operation}");
    return super.${listener.operation}(key, input);
  }
</#list>
}
