/*
 * Copyright 2022 EPAM Systems.
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

import com.epam.digital.data.platform.generator.constraints.impl.CompositeConstraintProvider;
import com.epam.digital.data.platform.generator.factory.AbstractEntityScopeFactory;
import com.epam.digital.data.platform.generator.metadata.EnumProvider;
import com.epam.digital.data.platform.generator.metadata.NestedReadEntity;
import com.epam.digital.data.platform.generator.metadata.NestedReadProvider;
import com.epam.digital.data.platform.generator.metadata.SearchConditionProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.model.template.Field;
import com.epam.digital.data.platform.generator.scope.ModelScope;
import com.epam.digital.data.platform.generator.utils.DbTypeConverter;
import com.epam.digital.data.platform.generator.utils.DbUtils;
import org.springframework.stereotype.Component;
import schemacrawler.schema.Column;
import schemacrawler.schema.Table;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

@Component
public class SearchConditionResponseScopeFactory extends AbstractEntityScopeFactory<ModelScope> {

  private final SearchConditionProvider searchConditionProvider;
  private final NestedReadProvider nestedReadProvider;
  private final CompositeConstraintProvider constraintProviders;

  public SearchConditionResponseScopeFactory(
      EnumProvider enumProvider,
      SearchConditionProvider searchConditionProvider,
      NestedReadProvider nestedReadProvider,
      CompositeConstraintProvider constraintProviders) {
    super(enumProvider);
    this.searchConditionProvider = searchConditionProvider;
    this.nestedReadProvider = nestedReadProvider;
    this.constraintProviders = constraintProviders;
  }

  @Override
  public List<ModelScope> create(Context context) {
    return context.getCatalog().getTables().stream()
        .filter(this::isSearchConditionsView)
        .map(
            table -> {
              var nestedEntitiesMap = nestedReadProvider.findFor(getCutTableName(table));
              var scope = new ModelScope();
              scope.setClassName(getSchemaName(table));
              List<Column> columns = identifyAllowedColumns(table);
              var nestedColumns =
                  columns.stream()
                      .filter(column -> nestedEntitiesMap.containsKey(column.getName()))
                      .collect(toList());
              var simpleColumns =
                  columns.stream().filter(Predicate.not(nestedColumns::contains)).collect(toList());
              scope.getFields().addAll(getNestedFields(nestedColumns, nestedEntitiesMap));
              scope.getFields().addAll(getSimpleFields(simpleColumns));
              return scope;
            })
        .collect(toList());
  }

  private List<Field> getSimpleFields(List<Column> columns) {
    return columns.stream()
        .map(
            column -> {
              var clazzName = DbTypeConverter.convertToJavaTypeName(column);

              var field = new Field();
              field.setName(getPropertyName(column));
              field.setType(typeToString(clazzName, column));
              field.setConstraints(
                  constraintProviders.getConstraintForProperty(column, clazzName));
              return field;
            })
        .collect(toList());
  }

  private List<Field> getNestedFields(
      List<Column> columns, Map<String, NestedReadEntity> nestedEntities) {
    return columns.stream()
        .map(
            column -> {
              var clazzName =
                  getSchemaName(nestedEntities.get(column.getName()).getRelatedTable())
                      + "ReadNested";

              var field = new Field();
              field.setName(getPropertyName(column));
              if (DbUtils.isColumnOfArrayType(column)) {
                field.setType(getArrayOfType(clazzName));
              } else {
                field.setType(clazzName);
              }
              field.setConstraints(
                  constraintProviders.getConstraintForProperty(column, clazzName));
              return field;
            })
        .collect(toList());
  }

  private List<Column> identifyAllowedColumns(Table table) {
    var sc = searchConditionProvider.findFor(getCutTableName(table));
    return table.getColumns().stream()
        .filter(
            column ->
                sc.getReturningColumns().contains(column.getName())
                    && DbUtils.isReadableColumn(column))
        .collect(toList());
  }

  @Override
  public String getPath() {
    return "model/src/main/java/model/dto/dto.java.ftl";
  }
}
