package ${basePackage}.kafkaapi.searchhandler;

import com.epam.digital.data.platform.kafkaapi.core.searchhandler.AbstractSearchHandler;
import org.jooq.Condition;
import org.jooq.SelectFieldOrAsterisk;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import ${basePackage}.model.dto.${schemaName}SearchConditionResponse;
import ${basePackage}.model.dto.${schemaName}SearchConditions;
<#if rls??>
import com.epam.digital.data.platform.restapi.core.exception.ForbiddenOperationException;
import com.epam.digital.data.platform.restapi.core.service.JwtInfoProvider;
import com.epam.digital.data.platform.starter.security.jwt.JwtClaimsUtils;
</#if>

@Service
public class ${className}
    extends AbstractSearchHandler<
        ${schemaName}SearchConditions,
        ${schemaName}SearchConditionResponse> {

<#if rls??>
  protected JwtInfoProvider jwtInfoProvider;

  public ${className}(
      JwtInfoProvider jwtInfoProvider) {
    super();
    this.jwtInfoProvider =  jwtInfoProvider;
  }
</#if>
<#if pagination?? && pagination.isTypePage()>
  @Override
  public com.epam.digital.data.platform.model.core.search.SearchConditionPage<${schemaName}SearchConditionResponse> search(
      com.epam.digital.data.platform.model.core.kafka.Request<${schemaName}SearchConditions> input) {
    var searchCriteria = input.getPayload();
    var response = super.search(input);
    response.setTotalElements(count(input));
    response.setPageSize(limit(searchCriteria));
    response.setTotalPages(com.epam.digital.data.platform.kafkaapi.core.util.PageableUtils
        .getTotalPages(response.getPageSize(), response.getTotalElements()));
    response.setPageNo(java.util.Optional.ofNullable(searchCriteria.getPageNo())
        .orElse(com.epam.digital.data.platform.kafkaapi.core.util.PageableUtils.DEFAULT_PAGE_NUMBER));
    return response;
  }
</#if>

  @Override
  protected Condition whereClause(${schemaName}SearchConditions searchConditions) {
    var c = DSL.noCondition();

  <#list equalFields as field>
    if (searchConditions.get${field.name?cap_first}() != null) {
      c = c.and(DSL.field("${field.columnName}").equal<#if field.ignoreCase>IgnoreCase</#if>(searchConditions.get${field.name?cap_first}())<#if enumSearchConditionFields?seq_contains(field.columnName)>.toString()</#if>);
    }
  </#list>
  <#list notEqualFields as field>
    if (searchConditions.get${field.name?cap_first}() != null) {
      c = c.and(DSL.field("${field.columnName}").notEqual<#if field.ignoreCase>IgnoreCase</#if>(searchConditions.get${field.name?cap_first}())<#if enumSearchConditionFields?seq_contains(field.columnName)>.toString()</#if>);
    }
  </#list>
  <#list startsWithFields as field>
    if (searchConditions.get${field.name?cap_first}() != null) {
      c = c.and(DSL.field("${field.columnName}").startsWith<#if field.ignoreCase>IgnoreCase</#if>(searchConditions.get${field.name?cap_first}()));
    }
  </#list>
  <#list startsWithArrayFields as field>
    if (searchConditions.get${field.name?cap_first}() != null) {
      var s = searchConditions.get${field.name?cap_first}().split(",");

      var inner = DSL.noCondition();
      for (String e: s) {
        inner = inner.or(DSL.field("${field.columnName}")
          .startsWith<#if field.ignoreCase>IgnoreCase</#if>(e)
          .toString());
      }

      c = c.and(inner);
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
  <#list notInFields as field>
    if (searchConditions.get${field.name?cap_first}() != null) {
    <#if field.ignoreCase>
      c = c.and(DSL.lower(DSL.field("${field.columnName}", String.class))
        .notIn(searchConditions.get${field.name?cap_first}().stream()
        .map(DSL::lower)
        .collect(Collectors.toList())));
    <#else>
      c = c.and(DSL.field("${field.columnName}").notIn(searchConditions.get${field.name?cap_first}()));
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
  protected String tableName() {
    return "${tableName}";
  }

  @Override
  protected Class<${schemaName}SearchConditionResponse> entityType() {
    return ${schemaName}SearchConditionResponse.class;
  }

  @Override
  protected List<SelectFieldOrAsterisk> selectFields() {
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

  @Override
  protected Integer limit(${schemaName}SearchConditions searchConditions) {
  <#if pagination?? && pagination.isTypeOffset()>
    <#if limit??>
    if (searchConditions.getLimit() != null) {
      return Math.min(searchConditions.getLimit(), ${limit?c});
    }
    return ${limit?c};
    <#else>
    return searchConditions.getLimit();
    </#if>
  <#elseif pagination?? && pagination.isTypePage()>
    <#if limit??>
    var pageSize =
      java.util.Optional.ofNullable(searchConditions.getPageSize())
        .orElse(com.epam.digital.data.platform.kafkaapi.core.util.PageableUtils.DEFAULT_PAGE_SIZE);
    return Math.min(pageSize, ${limit?c});
    <#else>
    return java.util.Optional.ofNullable(searchConditions.getPageSize())
        .orElse(com.epam.digital.data.platform.kafkaapi.core.util.PageableUtils.DEFAULT_PAGE_SIZE);
    </#if>
  <#else>
    <#if limit??>
    return ${limit?c};
    <#else>
    return null;
    </#if>
  </#if>
  }

  @Override
  protected Integer offset(${schemaName}SearchConditions searchConditions) {
  <#if pagination?? && pagination.isTypeOffset()>
    return searchConditions.getOffset();
  <#elseif pagination?? && pagination.isTypePage()>
    return java.util.Optional.ofNullable(limit(searchConditions)).orElse(0)
      * java.util.Optional.ofNullable(searchConditions.getPageNo())
        .orElse(com.epam.digital.data.platform.kafkaapi.core.util.PageableUtils.DEFAULT_PAGE_NUMBER);
  <#else>
    return null;
  </#if>
  }

<#if rls??>
  @Override
  protected Condition getCommonCondition(
      com.epam.digital.data.platform.model.core.kafka.Request<${schemaName}SearchConditions> input) {
    Condition condition = DSL.falseCondition();
    for (String d : JwtClaimsUtils.getAttributeValueAsStringList(jwtInfoProvider.getUserClaims(input), "${rls.jwtAttribute}")) {
      condition = condition.or(DSL.field("${rls.checkColumn}").startsWith(d));
    }

    return condition;
  }
</#if>
}