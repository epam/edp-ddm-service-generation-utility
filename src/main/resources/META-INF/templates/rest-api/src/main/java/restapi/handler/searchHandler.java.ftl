package ${basePackage}.restapi.handler;

import com.epam.digital.data.platform.restapi.core.searchhandler.AbstractSearchHandler;
import org.jooq.Condition;
import org.jooq.SelectFieldOrAsterisk;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import ${basePackage}.model.dto.${schemaName};
import ${basePackage}.model.dto.${schemaName}SearchConditions;

@Component
public class ${className}
    extends AbstractSearchHandler<
        ${schemaName}SearchConditions,
        ${schemaName}> {

  @Override
  protected Condition whereClause(${schemaName}SearchConditions searchConditions) {
    var c = DSL.noCondition();

  <#list equalFields as field>
    if (searchConditions.get${field.name?cap_first}() != null) {
      c = c.and(DSL.field("${field.columnName}").equal<#if field.ignoreCase>IgnoreCase</#if>(searchConditions.get${field.name?cap_first}())<#if enumSearchConditionFields?seq_contains(field.columnName)>.toString()</#if>);
    }
  </#list>
  <#list startsWithFields as field>
    if (searchConditions.get${field.name?cap_first}() != null) {
      c = c.and(DSL.field("${field.columnName}").startsWith<#if field.ignoreCase>IgnoreCase</#if>(searchConditions.get${field.name?cap_first}()));
    }
  </#list>
  <#list containsFields as field>
    if (searchConditions.get${field.name?cap_first}() != null) {
      c = c.and(DSL.field("${field.columnName}").contains<#if field.ignoreCase>IgnoreCase</#if>(searchConditions.get${field.name?cap_first}()));
    }
  </#list>
  <#list inFields as field>
    if (searchConditions.get${field.name?cap_first}() != null) {
    <#if field.ignoreCase>
      c = c.and(DSL.lower(DSL.field("${field.columnName}", String.class))
        .in(searchConditions.get${field.name?cap_first}().stream()
        .map(DSL::lower)
        .collect(Collectors.toList())));
    <#else>
      c = c.and(DSL.field("${field.columnName}").in(searchConditions.get${field.name?cap_first}()));
    </#if>
    }
  </#list>
  <#list betweenFields as field>
    if (searchConditions.get${field.name?cap_first}From() != null
        && searchConditions.get${field.name?cap_first}To() != null) {
    <#if field.ignoreCase>
      c = c.and(DSL.lower(DSL.field("${field.columnName}", String.class))
        .between(
          DSL.lower(searchConditions.get${field.name?cap_first}From()),
          DSL.lower(searchConditions.get${field.name?cap_first}To())));
    <#else>
      c = c.and(DSL.field("${field.columnName}")
        .between(
          searchConditions.get${field.name?cap_first}From(),
          searchConditions.get${field.name?cap_first}To()));
    </#if>
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
                .where(DSL.field("${group.pkName}").eq(DSL.field(tableName() + "." + "${column}")))
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
                .where(DSL.field("${group.pkName}").eq(DSL.any(DSL.array(DSL.field(tableName() + "." + "${column}")))))
            )
            .as("${column}")<#sep>,</#sep>
        </#list>
        )
    );
    return fields;
  }

  <#if pagination?? || limit??>
  @Override
  public Integer limit(${schemaName}SearchConditions searchConditions) {
  <#if pagination?? && limit??>
    if (searchConditions.getLimit() != null) {
      return Math.min(searchConditions.getLimit(), ${limit?c});
    }

    return ${limit?c};
  <#elseif pagination?? && !limit??>
    return searchConditions.getLimit();
  <#else>
    return ${limit?c};
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