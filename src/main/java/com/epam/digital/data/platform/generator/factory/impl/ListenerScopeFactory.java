package com.epam.digital.data.platform.generator.factory.impl;

import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.scope.ListenerScope;
import org.springframework.stereotype.Component;
import schemacrawler.schema.Table;
import com.epam.digital.data.platform.generator.factory.CrudAbstractScope;

@Component
public class ListenerScopeFactory extends CrudAbstractScope<ListenerScope> {

  @Override
  protected ListenerScope map(Table table, Context context) {
    var scope = new ListenerScope();
    scope.setClassName(getSchemaName(table) + "Listener");
    scope.setSchemaName(getSchemaName(table));
    scope.setPkType(getPkTypeName(table));

    var rootOfTopicName = toHyphenTableName(table);
    scope.addListener("update", rootOfTopicName, scope.getSchemaName(), "Void");
    scope.addListener("create", rootOfTopicName, scope.getSchemaName(),
        "com.epam.digital.data.platform.model.core.kafka.EntityId");
    scope.addListener("delete", rootOfTopicName, scope.getSchemaName(), "Void");

    return scope;
  }

  @Override
  public String getPath() {
    return "kafka-api/src/main/java/kafkaapi/listener/listener.java.ftl";
  }
}
