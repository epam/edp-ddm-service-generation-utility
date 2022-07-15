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

import com.epam.digital.data.platform.generator.constraints.impl.CompositeConstraintProvider;
import com.epam.digital.data.platform.generator.factory.AbstractEntityScopeFactory;
import com.epam.digital.data.platform.generator.metadata.EnumProvider;
import com.epam.digital.data.platform.generator.metadata.NestedReadEntity;
import com.epam.digital.data.platform.generator.metadata.NestedReadProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.model.template.Field;
import com.epam.digital.data.platform.generator.scope.ModelScope;
import com.epam.digital.data.platform.generator.utils.DbTypeConverter;
import com.epam.digital.data.platform.generator.utils.DbUtils;
import org.springframework.stereotype.Component;
import schemacrawler.schema.Column;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

@Component
public class ReadEntityScopeFactory extends AbstractEntityScopeFactory<ModelScope> {

  private final NestedReadProvider nestedReadProvider;
  private final CompositeConstraintProvider constraintProviders;

  public ReadEntityScopeFactory(
      EnumProvider enumProvider,
      NestedReadProvider nestedReadProvider,
      CompositeConstraintProvider constraintProviders) {
    super(enumProvider);
    this.nestedReadProvider = nestedReadProvider;
    this.constraintProviders = constraintProviders;
  }

  @Override
  public List<ModelScope> create(Context context) {
    return context.getCatalog().getTables().stream()
        .filter(this::isRecentDataTable)
        .map(
            table -> {
              var nestedEntitiesMap = nestedReadProvider.findFor(table.getName());
              var scope = new ModelScope();
              scope.setClassName(getSchemaName(table) + "Read");
              var nestedColumns =
                  table.getColumns().stream()
                      .filter(column -> nestedEntitiesMap.containsKey(column.getName()))
                      .collect(toList());
              var simpleColumns =
                  table.getColumns().stream()
                      .filter(Predicate.not(nestedColumns::contains))
                      .collect(toList());
              scope.getFields().addAll(getNestedFields(nestedColumns, nestedEntitiesMap));
              scope.getFields().addAll(getSimpleFields(simpleColumns));
              return scope;
            })
        .collect(toList());
  }

  private List<Field> getSimpleFields(List<Column> columns) {
    return columns.stream()
        .filter(DbUtils::isReadableColumn)
        .map(
            column -> {
              var clazzName = DbTypeConverter.convertToJavaTypeName(column);

              var field = new Field();
              field.setName(getPropertyName(column));
              field.setType(typeToString(clazzName, column));
              field.setConstraints(
                  constraintProviders.getConstraintForProperty(
                      column.getColumnDataType().getName(), clazzName));
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
                  constraintProviders.getConstraintForProperty(
                      column.getColumnDataType().getName(), clazzName));
              return field;
            })
        .collect(toList());
  }

  @Override
  public String getPath() {
    return "model/src/main/java/model/dto/dto.java.ftl";
  }
}
