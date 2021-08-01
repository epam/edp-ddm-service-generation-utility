package com.epam.digital.data.platform.generator.factory.impl;

import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.scope.QueryHandlerScope;
import org.springframework.stereotype.Component;
import schemacrawler.schema.Table;
import com.epam.digital.data.platform.generator.factory.CrudAbstractScope;

import static com.epam.digital.data.platform.generator.utils.ReadOperationUtils.getSelectableFields;

@Component
public class QueryHandlerScopeFactory extends CrudAbstractScope<QueryHandlerScope> {

  @Override
  protected QueryHandlerScope map(Table table, Context context) {
    QueryHandlerScope scope = new QueryHandlerScope();
    scope.setClassName(getSchemaName(table) + "QueryHandler");
    scope.setSchemaName(getSchemaName(table));
    scope.setPkColumnName(getPkColumn(table).getName());
    scope.setTableName(table.getName());
    scope.setPkType(getPkTypeName(table));
    scope.setOutputFields(getSelectableFields(table));
    return scope;
  }

  @Override
  public String getPath() {
    return "kafka-api/src/main/java/kafkaapi/queryhandler/impl/queryHandler.java.ftl";
  }
}
