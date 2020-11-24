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

import com.epam.digital.data.platform.generator.factory.SearchConditionsAbstractScope;
import com.epam.digital.data.platform.generator.metadata.EnumProvider;
import com.epam.digital.data.platform.generator.metadata.NestedReadEntity;
import com.epam.digital.data.platform.generator.metadata.NestedReadProvider;
import com.epam.digital.data.platform.generator.metadata.SearchConditionProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.model.template.NestedSelectableFieldsGroup;
import com.epam.digital.data.platform.generator.model.template.SearchConditionField;
import com.epam.digital.data.platform.generator.scope.SearchHandlerScope;
import com.epam.digital.data.platform.generator.utils.DbTypeConverter;
import com.epam.digital.data.platform.generator.utils.DbUtils;
import org.springframework.stereotype.Component;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.Table;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static com.epam.digital.data.platform.generator.utils.ReadOperationUtils.getSelectableFields;
import static com.epam.digital.data.platform.generator.utils.ReadOperationUtils.isAsyncSearchCondition;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Component
public class SearchHandlerScopeFactory extends SearchConditionsAbstractScope<SearchHandlerScope> {

  private final SearchConditionProvider searchConditionProvider;
  private final EnumProvider enumProvider;
  private final NestedReadProvider nestedReadProvider;

  public SearchHandlerScopeFactory(
      SearchConditionProvider searchConditionProvider,
      EnumProvider enumProvider,
      NestedReadProvider nestedReadProvider) {
    this.searchConditionProvider = searchConditionProvider;
    this.enumProvider = enumProvider;
    this.nestedReadProvider = nestedReadProvider;
  }

  @Override
  protected SearchHandlerScope map(Table table, Context context) {
    var sc = searchConditionProvider.findFor(getCutTableName(table));

    var equalFields =
        sc.getEqual().stream()
            .map(
                x ->
                    new SearchConditionField(
                        getPropertyName(x), x, isIgnoreCase(x, table)))
            .collect(toList());
    var containsFields =
        sc.getContains().stream()
            .map(x -> new SearchConditionField(getPropertyName(x), x, true))
            .collect(toList());
    var startsWithFields =
        sc.getStartsWith().stream()
            .map(x -> new SearchConditionField(getPropertyName(x), x, true))
            .collect(toList());
    var inFields =
        sc.getIn().stream()
            .map(
                x -> new SearchConditionField(getPropertyName(x), x, isIgnoreCase(x, table)))
            .collect(toList());
    var notInFields =
        sc.getNotIn().stream()
            .map(
                x -> new SearchConditionField(getPropertyName(x), x, isIgnoreCase(x, table)))
            .collect(toList());
    var betweenFields =
        sc.getBetween().stream()
            .map(x -> new SearchConditionField(getPropertyName(x), x, isIgnoreCase(x, table)))
            .collect(toList());
    Set<String> allEnums = enumProvider.findAllWithValues().keySet();
    List<String> enumSearchConditionFields = table.getColumns().stream()
        .filter(col -> allEnums.contains(col.getColumnDataType().getName()))
        .map(NamedObject::getName)
        .collect(toList());

    var nestedEntitiesMap = nestedReadProvider.findFor(getCutTableName(table));
    var simpleColumnNames =
        sc.getReturningColumns().stream()
            .filter(columnName -> !nestedEntitiesMap.containsKey(columnName))
            .collect(toList());
    var nestedColumnNames =
        sc.getReturningColumns().stream()
            .filter(columnName -> !simpleColumnNames.contains(columnName))
            .collect(toList());
    var singleElementNestedGroups =
        collectStreamToNestedGroup(
            nestedColumnNames.stream()
                .filter(columnName -> !DbUtils.isColumnOfArrayType(columnName, table))
                .collect(toList()),
            nestedEntitiesMap,
            context);
    var listElementNestedGroups =
        collectStreamToNestedGroup(
            nestedColumnNames.stream()
                .filter(columnName -> DbUtils.isColumnOfArrayType(columnName, table))
                .collect(toList()),
            nestedEntitiesMap,
            context);

    var scope = new SearchHandlerScope();
    scope.setClassName(getSchemaName(table) + "SearchHandler");
    scope.setSchemaName(getSchemaName(table));
    scope.setTableName(table.getName());
    scope.setLimit(sc.getLimit());
    scope.setEqualFields(equalFields);
    scope.setContainsFields(containsFields);
    scope.setStartsWithFields(startsWithFields);
    scope.setInFields(inFields);
    scope.setNotInFields(notInFields);
    scope.setBetweenFields(betweenFields);
    scope.setEnumSearchConditionFields(enumSearchConditionFields);
    scope.setSimpleSelectableFields(getSelectableFields(table, simpleColumnNames, context));
    scope.setNestedSingleSelectableGroups(singleElementNestedGroups);
    scope.setNestedListSelectableGroups(listElementNestedGroups);
    scope.setPagination(sc.getPagination());
    return scope;
  }

  private boolean isIgnoreCase(String columnName, Table table) {
    var column = table.getColumns().stream()
        .filter(x -> x.getName().equals(columnName))
        .findFirst()
        .orElseThrow(() -> new IllegalStateException(
            String.format(
                "Can not find in %s the column with name: %s",
                table.getName(),
                columnName)));

    boolean ignoreCase = false;

    String typeName = column.getColumnDataType().getName();
    boolean isEnum = !enumProvider.findFor(typeName).isEmpty();
    if (isEnum) {
      return false;
    }

    var type = DbTypeConverter.convertToJavaTypeName(column);
    if (String.class.getCanonicalName().equals(type)) {
      ignoreCase = true;
    }

    return ignoreCase;
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

  private Map<String, NestedSelectableFieldsGroup> collectStreamToNestedGroup(
      List<String> columnNames,
      Map<String, NestedReadEntity> nestedEntitiesMap,
      Context context) {
    return columnNames.stream().collect(
        toMap(
            Function.identity(),
            columnName -> getNestedGroupSelectables(nestedEntitiesMap.get(columnName), context),
            (el1, el2) -> el2));
  }

  @Override
  protected boolean isApplicable(Table table, Context context) {
    return super.isApplicable(table, context) &&
        !isAsyncSearchCondition(table.getName(), context);
  }

  @Override
  public String getPath() {
    return "rest-api/src/main/java/restapi/handler/searchHandler.java.ftl";
  }
}
