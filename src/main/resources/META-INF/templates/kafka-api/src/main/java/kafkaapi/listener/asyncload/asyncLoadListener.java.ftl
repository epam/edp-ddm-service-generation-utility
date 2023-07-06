package ${basePackage}.kafkaapi.listener.asyncload;

import com.epam.digital.data.platform.kafkaapi.core.listener.AsyncDataLoadKafkaListener;
import com.epam.digital.data.platform.kafkaapi.core.commandhandler.UpsertCommandHandler;
import com.epam.digital.data.platform.kafkaapi.core.service.CsvProcessor;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class ${className} extends AsyncDataLoadKafkaListener {

  private final Logger log = LoggerFactory.getLogger(${className}.class);

  private final static Map<String, String> ENTITY_NAMES_TO_SCHEMA_NAMES = Map.ofEntries(
    <#list entityNamesToSchemaNames as entityName, schemaName>
      Map.entry("${entityName}", "${schemaName}")<#sep>,
    </#list>
  );

  public ${className}(Map<String, CsvProcessor> csvProcessorMap,
      Map<String, UpsertCommandHandler> commandHandlerMap) {
    super(csvProcessorMap, commandHandlerMap, ENTITY_NAMES_TO_SCHEMA_NAMES);
  }

  @Override
  @KafkaListener(topics = "\u0023{kafkaProperties.topics['data-load-csv-inbound']}",
    filter = "${filterName}",
    groupId = "\u0023{kafkaProperties.consumer.groupId}",
    containerFactory = "concurrentKafkaListenerContainerFactory")
  public Message<String> asyncDataLoad(Message<String> input) {
    log.info("Kafka event received with create");
    var response = super.asyncDataLoad(input);
    log.info("create kafka event processing finished");
    return response;
  }
}
