package com.epam.digital.data.platform.generator.factory;

import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.scope.ServiceScope;
import org.apache.commons.lang3.StringUtils;
import schemacrawler.schema.Table;

public abstract class AbstractServiceScope<T extends ServiceScope> extends CrudAbstractScope<T> {

  protected abstract T instantiate();

  protected abstract String getOperation();

  @Override
  protected T map(Table table, Context context) {
    String modelName = getSchemaName(table);

    String pkName = getPkName(table);

    T scope = instantiate();

    scope.setClassName(modelName + StringUtils.capitalize(getOperation()) + "Service");
    scope.setSchemaName(modelName);
    scope.setPkName(pkName);
    scope.setPkType(getPkTypeName(table));

    String requestType = getOperation() + "-" + toHyphenTableName(table);
    scope.setRequestType(requestType);
    return scope;
  }
}
