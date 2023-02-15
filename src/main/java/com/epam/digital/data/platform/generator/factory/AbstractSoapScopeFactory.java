/*
 * Copyright 2021 EPAM Systems.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.epam.digital.data.platform.generator.factory;

import com.epam.digital.data.platform.generator.metadata.ExposeSearchConditionOption;
import com.epam.digital.data.platform.generator.metadata.SearchConditionPaginationType;
import com.epam.digital.data.platform.generator.metadata.SearchConditionProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.scope.SearchControllerScope;
import com.epam.digital.data.platform.generator.scope.SoapScope;
import com.epam.digital.data.platform.generator.utils.ScopeTypeUtils;
import schemacrawler.schema.Table;

import java.util.List;

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
    var searchConditionInfo = searchConditionProvider.findFor(getCutTableName(table.getName()));

    soapScope.addSchemaName(getSchemaName(table) + "SearchConditionResponse");
    soapScope.addSchemaName(getSchemaName(table) + "SearchConditions");

    var controllerScope = new SearchControllerScope();
    controllerScope.setSchemaName(getSchemaName(table));
    controllerScope.setEndpoint(getEndpoint(table.getName()));
    if (SearchConditionPaginationType.isTypePage(searchConditionInfo.getPagination())) {
      controllerScope.setResponseType(ScopeTypeUtils.SEARCH_CONDITION_PAGE_TYPE);
    } else {
      controllerScope.setResponseType(List.class.getCanonicalName());
    }

    soapScope.addSearchScopes(controllerScope);
    return soapScope;
  }

  private boolean isApplicable(Table table) {
    return searchConditionProvider
            .getExposedSearchConditions(ExposeSearchConditionOption.TREMBITA)
            .contains(getCutTableName(table))
        && isSearchConditionsView(table);
  }
}
