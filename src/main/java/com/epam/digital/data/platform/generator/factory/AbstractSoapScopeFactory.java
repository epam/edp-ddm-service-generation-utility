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
import com.epam.digital.data.platform.generator.scope.SoapSearchScope;
import com.epam.digital.data.platform.generator.utils.DbUtils;
import com.epam.digital.data.platform.generator.utils.ScopeTypeUtils;
import schemacrawler.schema.Table;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractSoapScopeFactory extends AbstractScope<SoapSearchScope> {

  private final SearchConditionProvider searchConditionProvider;

  protected AbstractSoapScopeFactory(SearchConditionProvider searchConditionProvider) {
    this.searchConditionProvider = searchConditionProvider;
  }

  @Override
  public List<SoapSearchScope> create(Context context) {
    var exposedSc = context.getCatalog().getTables().stream()
        .filter(this::isApplicable)
            .collect(Collectors.toList());

    var schemaNames = exposedSc.
            stream().flatMap(this::getRequiredSearchSchemaNames)
            .collect(Collectors.toSet());

    var searchScopes = exposedSc.stream()
            .map(this::getRequiredSearchScopes)
            .collect(Collectors.toList());

    var fileScopes = getFileScopes(exposedSc, context);

    var soapScope = new SoapSearchScope();
    soapScope.setSchemaNames(schemaNames);
    soapScope.setSearchScopes(searchScopes);
    soapScope.setFileScopes(fileScopes);

    return List.of(soapScope);
  }

  private Stream<String> getRequiredSearchSchemaNames(Table table) {
    return Stream.of(
        getSchemaName(table) + "SearchConditionResponse",
        getSchemaName(table) + "SearchConditions");
  }

  private SearchControllerScope getRequiredSearchScopes(Table table) {
    var searchConditionInfo = searchConditionProvider.findFor(getCutTableName(table.getName()));

    var searchControllerScope = new SearchControllerScope();
    searchControllerScope.setSchemaName(getSchemaName(table));
    searchControllerScope.setEndpoint(getEndpoint(table.getName()));
    if (SearchConditionPaginationType.isTypePage(searchConditionInfo.getPagination())) {
      searchControllerScope.setResponseType(ScopeTypeUtils.SEARCH_CONDITION_PAGE_TYPE);
    } else {
      searchControllerScope.setResponseType(List.class.getCanonicalName());
    }

    return searchControllerScope;
  }

  private List<SoapSearchScope.GetFileScopeInfo> getFileScopes(List<Table> exposedSc, Context context) {
    var anyExposedScContainFile =
            exposedSc.stream()
                    .map(Table::getColumns)
                    .flatMap(Collection::stream)
                    .anyMatch(DbUtils::isColumnFileOrFileArray);

    if (anyExposedScContainFile) {
      return context.getCatalog().getTables().stream()
                      .filter(this::isRecentDataTable)
                      .flatMap(
                              table -> {
                                var fileColumns =
                                        table.getColumns().stream()
                                                .filter(DbUtils::isColumnFileOrFileArray)
                                                .collect(Collectors.toList());

                                return fileColumns.stream()
                                        .map(
                                                column -> {
                                                  var scopeInfo = new SoapSearchScope.GetFileScopeInfo();
                                                  scopeInfo.setTableEndpoint(toHyphenTableName(table.getName()));
                                                  scopeInfo.setColumnEndpoint(toHyphenTableName(column.getName()));
                                                  scopeInfo.setPkType(getPkTypeName(table));
                                                  scopeInfo.setMethodName(
                                                          "getFiles"
                                                                  + getSchemaName(table)
                                                                  + getSchemaName(column.getName()));
                                                  return scopeInfo;
                                                });
                              })
                      .collect(Collectors.toList());
    }
    return Collections.emptyList();
  }

  private boolean isApplicable(Table table) {
    return searchConditionProvider
            .getExposedSearchConditionsByType(ExposeSearchConditionOption.TREMBITA)
            .contains(getCutTableName(table))
        && isSearchConditionsView(table);
  }
}
