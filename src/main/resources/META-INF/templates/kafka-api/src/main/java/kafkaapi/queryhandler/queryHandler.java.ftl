package ${basePackage}.kafkaapi.queryhandler;

import com.epam.digital.data.platform.kafkaapi.core.model.FieldsAccessCheckDto;
import com.epam.digital.data.platform.kafkaapi.core.queryhandler.AbstractQueryHandler;
import org.jooq.SelectFieldOrAsterisk;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;
import ${basePackage}.model.dto.${schemaName};
import ${basePackage}.kafkaapi.tabledata.${providerName};

@Service
public class ${className} extends
    AbstractQueryHandler<${pkType}, ${schemaName}> {

  public ${className}(
      ${providerName} tableDataProvider) {
    super(tableDataProvider);
  }

  @Override
  public Class<${schemaName}> entityType() {
    return ${schemaName}.class;
  }

  @Override
  public List<FieldsAccessCheckDto> getFieldsToCheckAccess() {
    return List.of(
    <#list tableAccessCheckFields as table, columns>
        new FieldsAccessCheckDto("${table}", List.of(
          <#list columns as columnName>
          "${columnName}"<#sep>,</#sep>
          </#list>
        ))<#sep>,</#sep>
    </#list>
    );
  }

  @Override
  public List<SelectFieldOrAsterisk> selectFields() {
    return Arrays.asList(
    <#list simpleSelectableFields as field>
        DSL.field("${field.name}"<#if field.converter??>, ${field.converter}</#if>)<#sep>,</#sep>
    </#list>
    <#if nestedSingleSelectableGroups?has_content>
        ,
    <#list nestedSingleSelectableGroups as column, group>
        DSL.field(
          DSL.select(
            DSL.jsonObject(
            <#list group.fields as field>
              DSL.field("${field.name}"<#if field.converter??>, ${field.converter}</#if>)<#sep>,</#sep>
            </#list>
            ))
          .from("${group.tableName}")
          .where(DSL.field("${group.pkName}").eq(DSL.field(tableDataProvider.tableName() + "." + "${column}"))))
        .as("${column}")
    </#list>
    </#if>
    <#if nestedListSelectableGroups?has_content>
        ,
    <#list nestedListSelectableGroups as column, group>
      DSL.field(
        DSL.select(
          DSL.coalesce(
            DSL.jsonArrayAgg(
              DSL.jsonObject(
              <#list group.fields as field>
                DSL.field("${field.name}"<#if field.converter??>, ${field.converter}</#if>)<#sep>,</#sep>
              </#list>
              )), DSL.jsonArray()))
        .from("${group.tableName}")
        .where(DSL.field("${group.pkName}").eq(DSL.any(DSL.array(DSL.field(tableDataProvider.tableName() + "." + "${column}"))))))
      .as("${column}")
    </#list>
    </#if>
    );
  }
}
