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
import com.epam.digital.data.platform.generator.metadata.SearchConditionProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.permissionmap.PermissionMap;
import com.epam.digital.data.platform.generator.scope.ControllerScope;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import org.springframework.stereotype.Component;
import schemacrawler.schema.Table;

@Component
public class SearchControllerScopeFactory extends SearchConditionsAbstractScope<ControllerScope> {

  private final SearchConditionProvider provider;
  private final PermissionMap permissionMap;

  public SearchControllerScopeFactory(
      SearchConditionProvider provider,
      PermissionMap permissionMap) {
    this.provider = provider;
    this.permissionMap = permissionMap;
  }

  @Override
  protected ControllerScope map(Table table, Context context) {
    var controllerScope = new ControllerScope();

    controllerScope.setClassName(getSchemaName(table) + "SearchController");
    controllerScope.setSchemaName(getSchemaName(table));
    controllerScope.setEndpoint(getEndpoint(table.getName()));

    controllerScope.setReadRoles(new ArrayList<>(findRolesForView(table)));

    return controllerScope;
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
    Function<String, List<String>> getReadExpressionsForColumn =
        column -> permissionMap.getReadExpressionsFor(tableName, column);

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
