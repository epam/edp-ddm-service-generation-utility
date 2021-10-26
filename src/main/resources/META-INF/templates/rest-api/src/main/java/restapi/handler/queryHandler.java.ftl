package ${basePackage}.restapi.handler;

import com.epam.digital.data.platform.restapi.core.queryhandler.AbstractQueryHandler;
import com.epam.digital.data.platform.restapi.core.service.AccessPermissionService;
import org.jooq.SelectFieldOrAsterisk;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.List;
import ${basePackage}.model.dto.${schemaName};

@Component
public class ${className} extends
    AbstractQueryHandler<${pkType}, ${schemaName}> {

  public ${className}(
      AccessPermissionService<${schemaName}> accessPermissionService) {
    super(accessPermissionService);
  }

  @Override
  public String idName() {
    return "${pkColumnName}";
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
    return Arrays.asList(
      <#list outputFields as field>
        DSL.field("${field.name}"<#if field.converter??>, ${field.converter}</#if>)<#sep>,</#sep>
      </#list>
        );
  }
}
