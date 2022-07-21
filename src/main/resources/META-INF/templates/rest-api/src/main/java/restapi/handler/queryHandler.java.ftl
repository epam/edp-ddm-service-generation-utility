package ${basePackage}.restapi.handler;

import com.epam.digital.data.platform.restapi.core.model.FieldsAccessCheckDto;
import com.epam.digital.data.platform.restapi.core.queryhandler.AbstractQueryHandler;
import org.jooq.SelectFieldOrAsterisk;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import ${basePackage}.model.dto.${schemaName};
import ${basePackage}.restapi.tabledata.${providerName};

@Component
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
    List<SelectFieldOrAsterisk> fields = new ArrayList<>();
    fields.addAll(
        Arrays.asList(
        <#list simpleSelectableFields as field>
            DSL.field("${field.name}"<#if field.converter??>, ${field.converter}</#if>)<#sep>,</#sep>
        </#list>
        )
    );
    fields.addAll(
        Arrays.asList(
        <#list nestedSingleSelectableGroups as column, group>
            DSL.field(
                DSL.select(
                    DSL.jsonObject(
                    <#list group.fields as field>
                        DSL.field("${field.name}"<#if field.converter??>, ${field.converter}</#if>)<#sep>,</#sep>
                    </#list>
                    )
                )
                .from("${group.tableName}")
                .where(DSL.field("${group.pkName}").eq(DSL.field(tableDataProvider.tableName() + "." + "${column}")))
            )
            .as("${column}")<#sep>,</#sep>
        </#list>
        )
    );
    fields.addAll(
        Arrays.asList(
        <#list nestedListSelectableGroups as column, group>
            DSL.field(
                DSL.select(
                    DSL.coalesce(
                        DSL.jsonArrayAgg(
                            DSL.jsonObject(
                            <#list group.fields as field>
                                DSL.field("${field.name}"<#if field.converter??>, ${field.converter}</#if>)<#sep>,</#sep>
                            </#list>
                            )
                        ),
                        DSL.jsonArray()
                    )
                )
                .from("${group.tableName}")
                .where(DSL.field("${group.pkName}").eq(DSL.any(DSL.array(DSL.field(tableDataProvider.tableName() + "." + "${column}")))))
            )
            .as("${column}")<#sep>,</#sep>
        </#list>
        )
    );
    return fields;
  }
}
