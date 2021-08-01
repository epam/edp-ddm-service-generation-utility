package com.epam.digital.data.platform.generator.factory;

import com.epam.digital.data.platform.generator.metadata.SearchConditionProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.scope.ControllerScope;
import com.epam.digital.data.platform.generator.scope.SoapScope;
import java.util.List;
import schemacrawler.schema.Table;

public abstract class AbstractSoapScopeFactory extends AbstractScope<SoapScope> {

  private final SearchConditionProvider searchConditionProvider;

  protected AbstractSoapScopeFactory(SearchConditionProvider searchConditionProvider) {
    this.searchConditionProvider = searchConditionProvider;
  }

  @Override
  public List<SoapScope> create(Context context) {
    var soapScope = new SoapScope();

    context.getCatalog().getTables().stream()
        .filter(this::isApplicable)
        .forEach(t -> map(t, soapScope));

    return List.of(soapScope);
  }


  private SoapScope map(Table table, SoapScope soapScope) {

    soapScope.addSchemaName(getSchemaName(table));
    soapScope.addSchemaName(getSchemaName(table) + "SearchConditions");

    var controllerScope = new ControllerScope();
    controllerScope.setSchemaName(getSchemaName(table));
    controllerScope.setEndpoint(getEndpoint(table.getName()));

    soapScope.addSearchScopes(controllerScope);
    return soapScope;
  }

  private boolean isApplicable(Table table) {
    return searchConditionProvider
        .getExposedSearchConditions()
        .contains(getCutTableName(table));
  }
}
