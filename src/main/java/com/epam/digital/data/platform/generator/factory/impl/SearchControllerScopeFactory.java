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

package com.epam.digital.data.platform.generator.factory.impl;

import static java.util.stream.Collectors.toSet;

import com.epam.digital.data.platform.generator.factory.SearchConditionsAbstractScope;
import com.epam.digital.data.platform.generator.metadata.NestedReadEntity;
import com.epam.digital.data.platform.generator.metadata.NestedReadProvider;
import com.epam.digital.data.platform.generator.metadata.SearchConditionPaginationType;
import com.epam.digital.data.platform.generator.metadata.SearchConditionProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.permissionmap.PermissionMap;
import com.epam.digital.data.platform.generator.permissionmap.PermissionObjectType;
import java.util.ArrayList;
import java.util.Collection;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.epam.digital.data.platform.generator.scope.SearchControllerScope;
import com.epam.digital.data.platform.generator.utils.ScopeTypeUtils;
import org.springframework.stereotype.Component;
import schemacrawler.schema.Table;

@Component
public class SearchControllerScopeFactory extends SearchConditionsAbstractScope<SearchControllerScope> {

  private final SearchConditionProvider provider;
  private final PermissionMap permissionMap;
  private final NestedReadProvider nestedReadProvider;

  public SearchControllerScopeFactory(
      SearchConditionProvider provider,
      PermissionMap permissionMap,
      NestedReadProvider nestedReadProvider) {
    this.provider = provider;
    this.permissionMap = permissionMap;
    this.nestedReadProvider = nestedReadProvider;
  }

  @Override
  protected SearchControllerScope map(Table table, Context context) {
    var searchConditionInfo = provider.findFor(getCutTableName(table.getName()));
    var controllerScope = new SearchControllerScope();

    controllerScope.setClassName(getSchemaName(table) + "SearchController");
    controllerScope.setSchemaName(getSchemaName(table));
    if (SearchConditionPaginationType.isTypePage(searchConditionInfo.getPagination())) {
      controllerScope.setResponseType(ScopeTypeUtils.SEARCH_CONDITION_PAGE_TYPE);
    } else {
      controllerScope.setResponseType(List.class.getCanonicalName());
    }
    controllerScope.setEndpoint(getEndpoint(table.getName()));
    controllerScope.setServiceName(getSchemaName(table) + "SearchService");
    controllerScope.setReadRoles(new ArrayList<>(findReadRoles(table)));

    return controllerScope;
  }

  private Set<String> findReadRoles(Table table) {
    String tableName = getCutTableName(table.getName());

    var nestedTables = findNestedTables(tableName);
    var nestedTablesPermissions = permissionMap.getReadExpressionsForTables(nestedTables);
    var viewPermissions = findRolesForView(table);
    var searchConditionPermission = permissionMap.getReadExpressionsForByType(tableName, PermissionObjectType.SEARCH_CONDITION);

    return Stream.of(viewPermissions, nestedTablesPermissions, searchConditionPermission)
        .flatMap(Collection::stream)
        .collect(Collectors.toSet());
  }

  private Set<String> findNestedTables(String tableName) {
    return nestedReadProvider.findFor(tableName).values().stream()
        .map(NestedReadEntity::getRelatedTable)
        .collect(Collectors.toSet());
  }

  private Set<String> findRolesForView(Table view) {
    Function<Entry<String, Set<String>>, Set<String>> findRolesForTable =
        entry -> findRolesForTable(entry.getKey(), entry.getValue());

    return provider.getTableColumnMapFor(getCutTableName(view)).entrySet().stream()
        .map(findRolesForTable)
        .flatMap(Collection::stream)
        .collect(toSet());
  }

  private Set<String> findRolesForTable(String tableName, Set<String> columns) {
    Function<String, Set<String>> getReadExpressionsForColumn =
        column -> permissionMap.getReadExpressionsForTable(tableName, column);

    return columns.stream()
        .map(getReadExpressionsForColumn)
        .flatMap(Collection::stream)
        .collect(toSet());
  }

  @Override
  public String getPath() {
    return "rest-api/src/main/java/restapi/controller/searchController.java.ftl";
  }
}
