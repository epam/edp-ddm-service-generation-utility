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

import static com.epam.digital.data.platform.generator.utils.ReadOperationUtils.getSelectableFields;
import static com.epam.digital.data.platform.generator.utils.ReadOperationUtils.isAsyncTable;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import com.epam.digital.data.platform.generator.factory.CrudAbstractScope;
import com.epam.digital.data.platform.generator.metadata.NestedReadEntity;
import com.epam.digital.data.platform.generator.metadata.NestedReadProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.model.template.NestedSelectableFieldsGroup;
import com.epam.digital.data.platform.generator.scope.QueryHandlerScope;
import com.epam.digital.data.platform.generator.utils.DbUtils;
import org.springframework.stereotype.Component;
import schemacrawler.schema.Column;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.Table;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Component
public class QueryHandlerScopeFactory extends CrudAbstractScope<QueryHandlerScope> {

  private final NestedReadProvider nestedReadProvider;

  public QueryHandlerScopeFactory(NestedReadProvider nestedReadProvider) {
    this.nestedReadProvider = nestedReadProvider;
  }

  @Override
  protected QueryHandlerScope map(Table table, Context context) {
    var nestedEntitiesMap = nestedReadProvider.findFor(table.getName());

    QueryHandlerScope scope = new QueryHandlerScope();
    scope.setClassName(getSchemaName(table) + "QueryHandler");
    scope.setSchemaName(getSchemaName(table) + "Read");
    scope.setPkColumnName(getPkColumn(table).getName());
    scope.setTableName(table.getName());
    scope.setPkType(getPkTypeName(table));
    scope.setProviderName(getSchemaName(table) + "TableDataProvider");
    scope.setTableAccessCheckFields(
        getPermissionCheckTableColumns(nestedEntitiesMap.values(), table, context));
    var nestedColumns =
        table.getColumns().stream()
            .filter(column -> nestedEntitiesMap.containsKey(column.getName()))
            .collect(toList());
    var simpleColumns =
        table.getColumns().stream()
            .filter(Predicate.not(nestedColumns::contains))
            .collect(toList());
    var singleElementNestedColumns =
        nestedColumns.stream()
            .filter(
                column -> !isManyToManyNestedColumn(nestedEntitiesMap.get(column.getName()), table))
            .collect(toList());
    var listElementNestedColumns =
        nestedColumns.stream()
            .filter(
                column -> isManyToManyNestedColumn(nestedEntitiesMap.get(column.getName()), table))
            .collect(toList());
    scope.setSimpleSelectableFields(getSelectableFields(table.getName(), simpleColumns, context));
    scope.setNestedSingleSelectableGroups(
        groupColumnsWithSelectableFields(singleElementNestedColumns, nestedEntitiesMap, context));
    scope.setNestedListSelectableGroups(
        groupColumnsWithSelectableFields(listElementNestedColumns, nestedEntitiesMap, context));
    scope.setRls(nestedReadProvider.getRlsMetadata(table.getName()));
    return scope;
  }

  private Map<String, List<String>> getPermissionCheckTableColumns(
      Collection<NestedReadEntity> nestedReadEntities, Table table, Context context) {
    var mainTableColumns = Map.of(table.getName(), getReadColumnNames(table));
    var joinedTableColumns =
        nestedReadEntities.stream()
            .collect(
                toMap(
                    NestedReadEntity::getRelatedTable,
                    nestedReadEntity ->
                        getReadColumnNames(findTable(nestedReadEntity.getRelatedTable(), context)),
                    (el1, el2) -> el2));
    return Stream.of(mainTableColumns, joinedTableColumns)
        .flatMap(map -> map.entrySet().stream())
        .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (el1, el2) -> el2));
  }

  private Map<String, NestedSelectableFieldsGroup> groupColumnsWithSelectableFields(
      List<Column> columns, Map<String, NestedReadEntity> nestedEntitiesMap, Context context) {
    return columns.stream()
        .map(NamedObject::getName)
        .collect(
            toMap(
                Function.identity(),
                columnName -> getNestedGroupSelectables(nestedEntitiesMap.get(columnName), context),
                (el1, el2) -> el2));
  }

  private NestedSelectableFieldsGroup getNestedGroupSelectables(
      NestedReadEntity nestedReadEntity, Context context) {
    var group = new NestedSelectableFieldsGroup();
    var relatedTable = findTable(nestedReadEntity.getRelatedTable(), context);
    group.setTableName(relatedTable.getName());
    group.setPkName(getPkColumn(relatedTable).getName());
    group.setFields(getSelectableFields(relatedTable, context));
    return group;
  }

  private boolean isManyToManyNestedColumn(NestedReadEntity nestedEntity, Table table) {
    var column = findColumn(nestedEntity.getColumn(), table);
    return DbUtils.isColumnOfArrayType(column);
  }

  private List<String> getReadColumnNames(Table table) {
    return table.getColumns().stream()
        .filter(DbUtils::isReadableColumn)
        .map(NamedObject::getName)
        .collect(toList());
  }

  @Override
  protected boolean isApplicable(Table table, Context context) {
    return super.isApplicable(table, context) && !isAsyncTable(table.getName(), context);
  }

  @Override
  public String getPath() {
    return "rest-api/src/main/java/restapi/handler/queryHandler.java.ftl";
  }
}
