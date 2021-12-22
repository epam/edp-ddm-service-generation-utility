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

import static java.util.stream.Collectors.toList;

import com.epam.digital.data.platform.generator.constraints.impl.CompositeConstraintProvider;
import com.epam.digital.data.platform.generator.factory.AbstractEntityScopeFactory;
import com.epam.digital.data.platform.generator.metadata.EnumProvider;
import com.epam.digital.data.platform.generator.metadata.SearchConditionProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.model.template.Field;
import com.epam.digital.data.platform.generator.scope.ModelScope;
import com.epam.digital.data.platform.generator.utils.DbTypeConverter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import schemacrawler.schema.Column;
import schemacrawler.schema.Table;

@Component
public class EntityScopeFactory extends AbstractEntityScopeFactory<ModelScope> {

  private final CompositeConstraintProvider constraintProviders;
  private final SearchConditionProvider searchConditionProvider;

  public EntityScopeFactory(
      EnumProvider enumProvider,
      CompositeConstraintProvider constraintProviders,
      SearchConditionProvider searchConditionProvider) {
    super(enumProvider);
    this.constraintProviders = constraintProviders;
    this.searchConditionProvider = searchConditionProvider;
  }

  @Override
  public List<ModelScope> create(Context context) {
    var scopes = new ArrayList<ModelScope>();

    scopes.addAll(createScopes(context));

    return scopes;
  }

  private List<ModelScope> createScopes(Context context) {
    return context.getCatalog().getTables().stream()
        .filter(table -> isRecentDataTable(table) || isSearchConditionsView(table))
        .map(t -> {
          var scope = new ModelScope();
          scope.setClassName(getSchemaName(t));
          List<Column> columns = identifyAllowedColumns(t);
          scope.getFields().addAll(getFields(columns));
          return scope;
        })
        .collect(toList());
  }

  private List<Field> getFields(List<Column> columns) {
    return columns.stream()
        .map(column -> {
          var clazzName = DbTypeConverter.convertToJavaTypeName(column);

          var constraints = constraintProviders.getConstraintForProperty(
              column.getColumnDataType().getName(), clazzName);

          var field = new Field();
          field.setName(getPropertyName(column));
          field.setType(typeToString(clazzName, column));
          field.setConstraints(constraints);
          return field;
        })
        .collect(toList());
  }

  private List<Column> identifyAllowedColumns(Table table) {
    if (isSearchConditionsView(table)) {
      var sc = searchConditionProvider.findFor(getCutTableName(table));
      return table.getColumns()
          .stream()
          .filter(column -> sc.getReturningColumns().contains(column.getName()))
          .collect(toList());
    } else {
      return table.getColumns();
    }
  }

  @Override
  public String getPath() {
    return "model/src/main/java/model/dto/dto.java.ftl";
  }
}
