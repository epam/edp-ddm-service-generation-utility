package ${basePackage}.kafkaapi.commandhandler.impl;

import com.epam.digital.data.platform.kafkaapi.core.commandhandler.Abstract${operation?capitalize}CommandHandler;
import com.epam.digital.data.platform.kafkaapi.core.commandhandler.util.EntityConverter;
import org.springframework.stereotype.Service;
import ${basePackage}.kafkaapi.tabledata.${providerName};
import ${basePackage}.model.dto.${schemaName};
<#if autoGeneratedValueFields?has_content>
import com.epam.digital.data.platform.model.core.kafka.Request;
import ${basePackage}.kafkaapi.sequencedata.${schemaName}SequenceDataProvider;
</#if>

@Service
public class ${className} extends Abstract${operation?capitalize}CommandHandler<${schemaName}> {

<#if autoGeneratedValueFields?has_content>
  private final ${schemaName}SequenceDataProvider sequenceProvider;
</#if>
  public ${className}(
      EntityConverter<${schemaName}> entityConverter,
      ${providerName} tableDataProvider<#if autoGeneratedValueFields?has_content>, 
      ${schemaName}SequenceDataProvider sequenceProvider</#if>) {
    super(entityConverter, tableDataProvider);
<#if autoGeneratedValueFields?has_content>
    this.sequenceProvider = sequenceProvider;
</#if>
  }

<#if autoGeneratedValueFields?has_content>
  public void requestPreprocessor(Request<${schemaName}> request) {
  <#list autoGeneratedValueFields as field>
    if (request.getPayload().get${field.name?cap_first}() == null) {
      request.getPayload().set${field.name?cap_first}(sequenceProvider.generate("${field.columnName}"));
    } else {
      sequenceProvider.validate("${field.columnName}", request.getPayload().get${field.name?cap_first}());
    }
  </#list>
  }
</#if>
}