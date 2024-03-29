package ${basePackage}.kafkaapi.commandhandler.impl;

import com.epam.digital.data.platform.kafkaapi.core.commandhandler.Abstract${operation?capitalize}CommandHandler;
import com.epam.digital.data.platform.kafkaapi.core.commandhandler.util.EntityConverter;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import ${basePackage}.kafkaapi.tabledata.${tableDataProviderName};
import ${basePackage}.model.dto.${schemaName};
<#if autoGeneratedValueFields?has_content>
import com.epam.digital.data.platform.model.core.kafka.Request;
import ${basePackage}.kafkaapi.sequencedata.${sequenceProviderName};
</#if>

@Service
public class ${className} extends Abstract${operation?capitalize}CommandHandler<${schemaName}> {

<#if autoGeneratedValueFields?has_content>
  private final ${sequenceProviderName} sequenceProvider;
</#if>
  private final boolean validateAutoGeneratedFields;
  public ${className}(
      EntityConverter<${schemaName}> entityConverter,
      ${tableDataProviderName} tableDataProvider<#if autoGeneratedValueFields?has_content>,
      ${sequenceProviderName} sequenceProvider</#if>,
      <#noparse>
      @Value("${data-platform.auto-generated-fields.validation.enabled}") boolean validateAutoGeneratedFields
      </#noparse>
      ) {
    super(entityConverter, tableDataProvider);
<#if autoGeneratedValueFields?has_content>
    this.sequenceProvider = sequenceProvider;
</#if>
    this.validateAutoGeneratedFields = validateAutoGeneratedFields;
  }

<#if autoGeneratedValueFields?has_content>
  public void requestPreprocessor(Request<${schemaName}> request) {
  <#list autoGeneratedValueFields as field>
    if (request.getPayload().get${field.name?cap_first}() == null) {
      request.getPayload().set${field.name?cap_first}(sequenceProvider.generate("${field.columnName}"));
    } else {
      if(validateAutoGeneratedFields) {
        sequenceProvider.validate("${field.columnName}", request.getPayload().get${field.name?cap_first}());
      }
    }
  </#list>
  }
</#if>
}
