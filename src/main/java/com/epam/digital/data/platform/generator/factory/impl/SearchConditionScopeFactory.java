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

import static java.lang.Boolean.TRUE;
import static java.util.Collections.emptyList;
import static java.util.List.of;
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
import schemacrawler.schema.Table;

@Component
public class SearchConditionScopeFactory extends AbstractEntityScopeFactory<ModelScope> {

  private final SearchConditionProvider searchConditionProvider;
  private final CompositeConstraintProvider constraintProviders;

  public SearchConditionScopeFactory(
      SearchConditionProvider searchConditionProvider,
      EnumProvider enumProvider,
      CompositeConstraintProvider constraintProviders) {
    super(enumProvider);
    this.searchConditionProvider = searchConditionProvider;
    this.constraintProviders = constraintProviders;
  }

  @Override
  public List<ModelScope> create(Context context) {
    var scopes = new ArrayList<ModelScope>();

    scopes.addAll(createSearchConditionScopes(context));

    return scopes;
  }

  private List<ModelScope> createSearchConditionScopes(Context context) {
    return context.getCatalog().getTables().stream()
        .filter(this::isSearchConditionsView)
        .map(t -> {
          var scope = new ModelScope();
          scope.setClassName(getSchemaName(t) + "SearchConditions");
          scope.getFields().addAll(getSearchConditionFields(t));
          return scope;
        })
        .collect(toList());
  }

  private List<Field> getSearchConditionFields(Table table) {
    var searchConditions = searchConditionProvider.findFor(getCutTableName(table));

    List<Field> fields = new ArrayList<>();
    fields.addAll(searchConditions.getEqual().stream()
            .map(sc -> mapColumnConditionToField(table, sc, false))
            .collect(toList()));
    fields.addAll(searchConditions.getContains().stream()
            .map(sc -> mapColumnConditionToField(table, sc, false))
            .collect(toList()));
    fields.addAll(searchConditions.getStartsWith().stream()
            .map(sc -> mapColumnConditionToField(table, sc, false))
            .collect(toList()));
    fields.addAll(searchConditions.getIn().stream()
            .map(sc -> mapColumnConditionToField(table, sc, true))
            .collect(toList()));

    if (TRUE.equals(searchConditions.getPagination())) {
      fields.addAll(of(
          getAuxiliaryField("limit", Integer.class),
          getAuxiliaryField("offset", Integer.class)));
    }

    return fields;
  }

  private Field mapColumnConditionToField(
      Table table, String columnName, boolean isCollectionSearch) {
    var column = findColumn(columnName, table);

    var clazzName = DbTypeConverter.convertToJavaTypeName(column);

    var constraints =
        constraintProviders
            .getFormattingConstraintProvider()
            .getConstraintForProperty(column.getColumnDataType().getName(), clazzName);

    constraints.addAll(
        constraintProviders
            .getMarshalingConstraintProvider()
            .getConstraintForProperty(column.getColumnDataType().getName(), clazzName));

    var field = new Field();
    field.setName(getPropertyName(columnName));
    if (isCollectionSearch) {
      field.setType(typeToStringForCollection(clazzName, column));
    } else {
      field.setType(typeToString(clazzName, column));
    }
    field.setConstraints(constraints);
    return field;
  }

  private Field getAuxiliaryField(String name, Class<?> clazz) {
    var field = new Field();
    field.setName(name);
    field.setType(clazz.getCanonicalName());
    field.setConstraints(emptyList());
    return field;
  }

  @Override
  public String getPath() {
    return "model/src/main/java/model/dto/dto.java.ftl";
  }
}