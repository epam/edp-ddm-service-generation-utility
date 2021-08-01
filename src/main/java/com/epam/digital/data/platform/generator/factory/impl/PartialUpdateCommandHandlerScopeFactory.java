package com.epam.digital.data.platform.generator.factory.impl;

import static java.util.stream.Collectors.toList;

import com.epam.digital.data.platform.generator.factory.AbstractScope;
import com.epam.digital.data.platform.generator.metadata.PartialUpdateProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.scope.CommandHandlerScope;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PartialUpdateCommandHandlerScopeFactory extends AbstractScope<CommandHandlerScope> {

  private final PartialUpdateProvider provider;

  public PartialUpdateCommandHandlerScopeFactory(
      PartialUpdateProvider provider) {
    this.provider = provider;
  }

  @Override
  public List<CommandHandlerScope> create(Context context) {
    return provider.findAll().stream()
        .map(upd -> {
          var table = findTable(upd.getTableName(), context);

          var scope = new CommandHandlerScope();
          scope.setClassName(getSchemaName(table, upd.getName()) + "CommandHandler");
          scope.setSchemaName(getSchemaName(table, upd.getName()));
          scope.setPkColumnName(getPkColumn(table).getName());
          scope.setTableName(table.getName());

          return scope;
        })
        .collect(toList());
  }

  @Override
  public String getPath() {
    return "kafka-api/src/main/java/kafkaapi/commandhandler/impl/commandHandler.java.ftl";
  }
}
