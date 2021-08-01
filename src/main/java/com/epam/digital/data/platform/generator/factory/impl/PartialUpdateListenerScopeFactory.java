package com.epam.digital.data.platform.generator.factory.impl;

import static java.util.stream.Collectors.toList;

import com.epam.digital.data.platform.generator.factory.AbstractScope;
import com.epam.digital.data.platform.generator.metadata.PartialUpdateProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.scope.ListenerScope;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PartialUpdateListenerScopeFactory extends AbstractScope<ListenerScope> {

  private final PartialUpdateProvider provider;

  public PartialUpdateListenerScopeFactory(
      PartialUpdateProvider provider) {
    this.provider = provider;
  }

  @Override
  public List<ListenerScope> create(Context context) {
    return provider.findAll().stream()
        .map(upd -> {
          var table = findTable(upd.getTableName(), context);

          var scope = new ListenerScope();
          scope.setClassName(getSchemaName(table, upd.getName()) + "Listener");
          scope.setSchemaName(getSchemaName(table, upd.getName()));
          scope.setPkType(getPkTypeName(table));

          var rootOfTopicName = toHyphenTableName(table) + "-" + toHyphenTableName(upd.getName());
          scope.addListener("update", rootOfTopicName, scope.getSchemaName(), "Void");

          return scope;
        })
        .collect(toList());
  }

  @Override
  public String getPath() {
    return "kafka-api/src/main/java/kafkaapi/listener/commandListener.java.ftl";
  }
}
