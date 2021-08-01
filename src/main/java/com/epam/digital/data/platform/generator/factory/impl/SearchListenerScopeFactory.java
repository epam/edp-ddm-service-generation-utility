package com.epam.digital.data.platform.generator.factory.impl;

import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.scope.ListenerScope;
import org.springframework.stereotype.Component;
import schemacrawler.schema.Table;
import com.epam.digital.data.platform.generator.factory.SearchConditionsAbstractScope;

@Component
public class SearchListenerScopeFactory extends SearchConditionsAbstractScope<ListenerScope> {

  @Override
  protected ListenerScope map(Table table, Context context) {
    var scope = new ListenerScope();
    scope.setClassName(getSchemaName(table) + "Listener");
    scope.setSchemaName(getSchemaName(table));

    scope.addListener("search", toHyphenTableName(table), scope.getSchemaName(), scope.getSchemaName());

    return scope;
  }

  @Override
  public String getPath() {
    return "kafka-api/src/main/java/kafkaapi/listener/search/searchListener.java.ftl";
  }
}
