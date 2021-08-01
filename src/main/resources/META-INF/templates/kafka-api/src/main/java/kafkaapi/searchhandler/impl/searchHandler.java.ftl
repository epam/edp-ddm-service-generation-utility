package ${basePackage}.kafkaapi.searchhandler.impl;

import com.epam.digital.data.platform.kafkaapi.core.searchhandler.AbstractSearchHandler;
import org.jooq.Condition;
import org.jooq.SelectFieldOrAsterisk;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;
import ${basePackage}.model.dto.${schemaName};
import ${basePackage}.model.dto.${schemaName}SearchConditions;

@Service
public class ${schemaName}SearchHandler extends
    AbstractSearchHandler<${schemaName}SearchConditions, ${schemaName}> {

  @Override
  protected Condition whereClause(${schemaName}SearchConditions searchConditions) {
    var c = DSL.noCondition();

  <#list searchConditionFields as field>
    if (searchConditions.get${field.name?cap_first}() != null) {
      c = c.and(DSL.field("${field.columnName}").${field.operation}(searchConditions.get${field.name?cap_first}())<#if enumSearchConditionFields?seq_contains(field.columnName)>.toString()</#if>);
    }
  </#list>

    return c;
  }

  @Override
  public String tableName() {
    return "${tableName}";
  }

  @Override
  public Class<${schemaName}> entityType() {
    return ${schemaName}.class;
  }

  @Override
  public List<SelectFieldOrAsterisk> selectFields(){
    return Arrays.asList(
      <#list outputFields as field>
        DSL.field("${field.name}"<#if field.converter??>, ${field.converter}</#if>)<#sep>,</#sep>
      </#list>
        );
  }

  <#if pagination?? || limit??>
  @Override
  public Integer limit(${schemaName}SearchConditions searchConditions) {
  <#if pagination?? && limit??>
    if (searchConditions.getLimit() != null) {
      return Math.min(searchConditions.getLimit(), ${limit});
    }

    return ${limit};
  <#elseif pagination?? && !limit??>
    return searchConditions.getLimit();
  <#else>
    return ${limit};
  </#if>
  }
  </#if>
  <#if pagination??>
  @Override
  public Integer offset(${schemaName}SearchConditions searchConditions) {
    return searchConditions.getOffset();
  }
  </#if>
}