package com.epam.digital.data.platform.generator.factory.impl;

import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.scope.CommandHandlerScope;
import org.springframework.stereotype.Component;
import schemacrawler.schema.Table;
import com.epam.digital.data.platform.generator.factory.CrudAbstractScope;

@Component
public class CommandHandlerScopeFactory extends CrudAbstractScope<CommandHandlerScope> {

  @Override
  protected CommandHandlerScope map(Table table, Context context) {
    CommandHandlerScope scope = new CommandHandlerScope();
    scope.setClassName(getSchemaName(table) + "CommandHandler");
    scope.setSchemaName(getSchemaName(table));
    scope.setPkColumnName(getPkColumn(table).getName());
    scope.setTableName(table.getName());
    return scope;
  }

  @Override
  public String getPath() {
    return "kafka-api/src/main/java/kafkaapi/commandhandler/impl/commandHandler.java.ftl";
  }
}
