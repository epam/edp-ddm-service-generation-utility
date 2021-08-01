package com.epam.digital.data.platform.generator.factory.impl;

import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.scope.ServiceScope;
import org.springframework.stereotype.Component;
import schemacrawler.schema.Table;
import com.epam.digital.data.platform.generator.factory.SearchConditionsAbstractScope;
import com.epam.digital.data.platform.generator.scope.SearchServiceScope;

@Component
public class SearchServiceScopeFactory extends SearchConditionsAbstractScope<ServiceScope> {

  @Override
  protected ServiceScope map(Table table, Context context) {
    var serviceScope = new SearchServiceScope();
    serviceScope.setClassName(getSchemaName(table) + "SearchService");
    serviceScope.setSchemaName(getSchemaName(table));
    serviceScope.setRequestType("search" + "-" + toHyphenTableName(table.getName()));
    return serviceScope;
  }

  @Override
  public String getPath() {
    return "rest-api/src/main/java/restapi/service/search.java.ftl";
  }
}
