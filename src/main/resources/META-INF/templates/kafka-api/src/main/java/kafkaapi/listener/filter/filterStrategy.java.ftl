package ${basePackage}.kafkaapi.listener.filter;

import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.commons.text.CaseUtils;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class ${className}
        implements RecordFilterStrategy {

  private static final Set<String> ALLOWED_ENTITIES = Set.of(
    <#list allowedEntities as entityName>
    "${entityName}"<#sep>,
    </#list>
  );

  private final Logger log = LoggerFactory.getLogger(${className}.class);

  @Override
  public boolean filter(ConsumerRecord consumerRecord) {
      log.info("Start filtering consumer record");
      byte[] entityName = consumerRecord.headers().lastHeader("EntityName").value();
      String entityNameString = new String(entityName, StandardCharsets.UTF_8);
      log.debug("With record entity name header: " + entityNameString);

      if (entityNameString.startsWith("nested/")) {
        entityNameString = entityNameString.substring("nested/".length());
      }

      return !ALLOWED_ENTITIES.contains(CaseUtils.toCamelCase(entityNameString, true, '-', '_'));
  }

}