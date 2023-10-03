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
import com.epam.digital.data.platform.generator.metadata.SearchConditionOperation;
import com.epam.digital.data.platform.generator.metadata.SearchConditionOperationTree;
import com.epam.digital.data.platform.generator.metadata.SearchConditionProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.model.template.NestedSelectableFieldsGroup;
import com.epam.digital.data.platform.generator.model.template.RlsFieldRestriction;
import com.epam.digital.data.platform.generator.model.template.SearchConditionField;
import com.epam.digital.data.platform.generator.model.template.SearchOperation;
import com.epam.digital.data.platform.generator.model.template.SearchOperatorType;
import com.epam.digital.data.platform.generator.model.template.SearchType;
import com.epam.digital.data.platform.generator.scope.SearchHandlerScope;
import com.epam.digital.data.platform.generator.utils.DbTypeConverter;
import com.epam.digital.data.platform.generator.utils.DbUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.Table;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    var columnToSearchTypeMap = sc.getColumnToSearchType();

    var topLevelSearchOperations = Optional.ofNullable(sc.getSearchOperationTree())
            .map(SearchConditionOperationTree::getOperations)
            .orElse(Collections.emptyList())
            .stream()
            .flatMap(op -> op.getLogicOperators().stream())
            .collect(toList());
    var innerLogicOperationColumns = getInnerLogicOperationColumns(topLevelSearchOperations);

    var topLevelSearchColumns = columnToSearchTypeMap.keySet().stream()
            .filter(searchType -> !innerLogicOperationColumns.contains(searchType))
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

    var rootLogicOperator = new SearchConditionOperation.LogicOperator();
    rootLogicOperator.setColumns(topLevelSearchColumns);
    rootLogicOperator.setType(SearchOperatorType.AND);
    rootLogicOperator.setLogicOperators(topLevelSearchOperations);
    var searchOperation =
        getSearchOperation("mainCondition", columnToSearchTypeMap, rootLogicOperator, table);
    scope.setSearchLogicOperations(Collections.singletonList(searchOperation));
    scope.setEnumSearchConditionFields(enumSearchConditionFields);
    scope.setSimpleSelectableFields(getSelectableFields(table, simpleColumnNames, context));
    scope.setNestedSingleSelectableGroups(singleElementNestedGroups);
    scope.setNestedListSelectableGroups(listElementNestedGroups);
    scope.setPagination(sc.getPagination());
    scope.setRls(getRlsRestriction(table.getName()));
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

  private Set<String> getInnerLogicOperationColumns(
      List<SearchConditionOperation.LogicOperator> logicOperators) {
    if (CollectionUtils.isEmpty(logicOperators)) {
      return Collections.emptySet();
    }
    var currLevelColumns = logicOperators.stream().flatMap(cond -> cond.getColumns().stream());
    var nestedLevelColumns =
        logicOperators.stream()
            .flatMap(cond -> getInnerLogicOperationColumns(cond.getLogicOperators()).stream());
    return Stream.concat(currLevelColumns, nestedLevelColumns).collect(Collectors.toSet());
  }

  private SearchOperation getSearchOperation(
      String operationName,
      Map<String, SearchType> columnToSearchTypeMap,
      SearchConditionOperation.LogicOperator currentCondition,
      Table table) {
    var equalFields =
        currentCondition.getColumns().stream()
            .filter(column -> SearchType.EQUAL.equals(columnToSearchTypeMap.get(column)))
            .map(
                column ->
                    new SearchConditionField(
                        getPropertyName(column), column, isIgnoreCase(column, table)))
            .collect(toList());
    var notEqualFields =
            currentCondition.getColumns()
                    .stream()
                    .filter(column -> SearchType.NOT_EQUAL.equals(columnToSearchTypeMap.get(column)))
                    .map(
                            column ->
                                    new SearchConditionField(
                                            getPropertyName(column), column, isIgnoreCase(column, table)))
                    .collect(toList());
    var containsFields =
            currentCondition.getColumns()
                    .stream()
                    .filter(column -> SearchType.CONTAINS.equals(columnToSearchTypeMap.get(column)))
                    .map(column -> new SearchConditionField(getPropertyName(column), column, true))
                    .collect(toList());
    var startsWithFields =
            currentCondition.getColumns()
                    .stream()
                    .filter(column -> SearchType.STARTS_WITH.equals(columnToSearchTypeMap.get(column)))
                    .map(column -> new SearchConditionField(getPropertyName(column), column, true))
                    .collect(toList());
    var startsWithArrayFields =
            currentCondition.getColumns()
                    .stream()
                    .filter(column -> SearchType.STARTS_WITH_ARRAY.equals(columnToSearchTypeMap.get(column)))
                    .map(column -> new SearchConditionField(getPropertyName(column), column, true))
                    .collect(toList());
    var inFields =
            currentCondition.getColumns()
                    .stream()
                    .filter(column -> SearchType.IN.equals(columnToSearchTypeMap.get(column)))
                    .map(
                            column ->
                                    new SearchConditionField(
                                            getPropertyName(column), column, isIgnoreCase(column, table)))
                    .collect(toList());
    var notInFields =
            currentCondition.getColumns()
                    .stream()
                    .filter(column -> SearchType.NOT_IN.equals(columnToSearchTypeMap.get(column)))
                    .map(
                            column ->
                                    new SearchConditionField(
                                            getPropertyName(column), column, isIgnoreCase(column, table)))
                    .collect(toList());
    var betweenFields =
            currentCondition.getColumns()
                    .stream()
                    .filter(column -> SearchType.BETWEEN.equals(columnToSearchTypeMap.get(column)))
                    .map(
                            column ->
                                    new SearchConditionField(
                                            getPropertyName(column), column, isIgnoreCase(column, table)))
                    .collect(toList());

    var nestedSearchOperations =
        Optional.ofNullable(currentCondition.getLogicOperators())
            .orElse(Collections.emptyList())
            .stream()
            .map(
                innerLogicOperator -> {
                  var columnsAppendix =
                      innerLogicOperator.getColumns().stream()
                          .map(this::getSchemaName)
                          .collect(Collectors.joining());
                  var randomSuffixAppendix = RandomStringUtils.randomNumeric(5);
                  var name =
                      innerLogicOperator.getType().toString().toLowerCase()
                          + columnsAppendix
                          + randomSuffixAppendix;
                  return getSearchOperation(name, columnToSearchTypeMap, innerLogicOperator, table);
                })
            .collect(toList());

    var searchOperation = new SearchOperation();
    searchOperation.setOperator(currentCondition.getType());
    searchOperation.setOperationName(operationName);
    searchOperation.setEqualFields(equalFields);
    searchOperation.setNotEqualFields(notEqualFields);
    searchOperation.setContainsFields(containsFields);
    searchOperation.setStartsWithFields(startsWithFields);
    searchOperation.setStartsWithArrayFields(startsWithArrayFields);
    searchOperation.setInFields(inFields);
    searchOperation.setNotInFields(notInFields);
    searchOperation.setBetweenFields(betweenFields);
    searchOperation.setNestedSearchOperations(nestedSearchOperations);
    return searchOperation;
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

  private RlsFieldRestriction getRlsRestriction(String tableName) {
    return Optional.ofNullable(searchConditionProvider.getRlsMetadata(tableName))
        .map(
            rlsMetadata -> {
              var restriction = new RlsFieldRestriction();
              restriction.setCheckTable(rlsMetadata.getCheckTable());
              restriction.setCheckColumn(rlsMetadata.getCheckColumn());
              restriction.setJwtAttribute(rlsMetadata.getJwtAttribute());
              restriction.setCheckField(getPropertyName(rlsMetadata.getCheckColumn()));
              return restriction;
            })
        .orElse(null);
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
