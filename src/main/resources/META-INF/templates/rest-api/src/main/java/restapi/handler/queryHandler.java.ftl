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
<#if rls??>
import com.epam.digital.data.platform.model.core.kafka.Request;
import com.epam.digital.data.platform.restapi.core.exception.ForbiddenOperationException;
import com.epam.digital.data.platform.restapi.core.service.JwtInfoProvider;
import com.epam.digital.data.platform.starter.security.exception.JwtClaimIncorrectAttributeException;
import com.epam.digital.data.platform.starter.security.jwt.JwtClaimsUtils;
</#if>

@Component
public class ${className} extends
    AbstractQueryHandler<${pkType}, ${schemaName}> {

<#if rls??>

    protected JwtInfoProvider jwtInfoProvider;

    public ${className}(
    ${providerName} tableDataProvider,  JwtInfoProvider jwtInfoProvider) {
        super(tableDataProvider);
        this.jwtInfoProvider =  jwtInfoProvider;
    }
<#else>
    public ${className}(
    ${providerName} tableDataProvider) {
        super(tableDataProvider);
    }
</#if>

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

  <#if rls??>
  @Override
  public Condition getCommonCondition(Request<${pkType}> input) {
    var condition = DSL.noCondition();
        try {
            for (String d : JwtClaimsUtils.getAttributeValueAsStringList(jwtInfoProvider.getUserClaims(input), "${rls.jwtAttribute}")) {
              condition = condition.or(DSL.field("${rls.checkColumn}").startsWith(d));
            }
        } catch (JwtClaimIncorrectAttributeException e) {
            var claims = jwtInfoProvider.getUserClaims(input);
            throw new ForbiddenOperationException("User <" + claims.getDrfo() + ":" + claims.getEdrpou() +
                "> dont have the required security attribute ${rls.jwtAttribute}");
        }
    return condition;
  }
  </#if>

}
