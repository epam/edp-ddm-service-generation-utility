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

import static java.util.Collections.emptyList;
import static java.util.List.of;
import static java.util.stream.Collectors.toList;

import com.epam.digital.data.platform.generator.constraints.ConstraintProvider;
import com.epam.digital.data.platform.generator.constraints.impl.CompositeConstraintProvider;
import com.epam.digital.data.platform.generator.constraints.impl.RequiredConstraintProvider;
import com.epam.digital.data.platform.generator.constraints.impl.SingleValueAsArrayConstraintProvider;
import com.epam.digital.data.platform.generator.factory.AbstractEntityScopeFactory;
import com.epam.digital.data.platform.generator.metadata.EnumProvider;
import com.epam.digital.data.platform.generator.metadata.SearchConditionPaginationType;
import com.epam.digital.data.platform.generator.metadata.SearchConditionProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.model.template.Field;
import com.epam.digital.data.platform.generator.model.template.SearchType;
import com.epam.digital.data.platform.generator.scope.ModelScope;
import com.epam.digital.data.platform.generator.utils.DbTypeConverter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import com.epam.digital.data.platform.generator.utils.ScopeTypeUtils;
import org.springframework.stereotype.Component;
import schemacrawler.schema.Column;
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
    var requiredFields = searchConditions.getRequiredColumns();
    var columnToSearchTypeMap = searchConditions.getColumnToSearchType();
    List<Field> fields = new ArrayList<>();
    fields.addAll(
        getColumnsOfSearchType(columnToSearchTypeMap, SearchType.EQUAL)
            .map(sc -> new ColumnConditionMapping(table, sc, getPropertyName(sc), requiredFields.contains(sc)))
            .map(it -> mapColumnConditionToField(it, getCommonConstraintProviders(it.isRequired), this::typeToString))
            .collect(toList()));
    fields.addAll(
        getColumnsOfSearchType(columnToSearchTypeMap, SearchType.NOT_EQUAL)
            .map(sc -> new ColumnConditionMapping(table, sc, getPropertyName(sc), requiredFields.contains(sc)))
            .map(it -> mapColumnConditionToField(it, getCommonConstraintProviders(it.isRequired), this::typeToString))
            .collect(toList()));
    fields.addAll(
        getColumnsOfSearchType(columnToSearchTypeMap, SearchType.CONTAINS)
            .map(sc -> new ColumnConditionMapping(table, sc, getPropertyName(sc), requiredFields.contains(sc)))
            .map(it -> mapColumnConditionToField(it, getCommonConstraintProviders(it.isRequired), this::typeToString))
            .collect(toList()));
    fields.addAll(
        getColumnsOfSearchType(columnToSearchTypeMap, SearchType.STARTS_WITH)
            .map(sc -> new ColumnConditionMapping(table, sc, getPropertyName(sc), requiredFields.contains(sc)))
            .map(it -> mapColumnConditionToField(it, getCommonConstraintProviders(it.isRequired), this::typeToString))
            .collect(toList()));
    fields.addAll(
        getColumnsOfSearchType(columnToSearchTypeMap, SearchType.STARTS_WITH_ARRAY)
            .map(sc -> new ColumnConditionMapping(table, sc, getPropertyName(sc), requiredFields.contains(sc)))
            .map(it -> mapColumnConditionToField(it, getCommonConstraintProviders(it.isRequired), this::typeToString))
            .collect(toList()));
    fields.addAll(
        getColumnsOfSearchType(columnToSearchTypeMap, SearchType.IN)
            .map(sc -> new ColumnConditionMapping(table, sc, getPropertyName(sc), requiredFields.contains(sc)))
            .map(it -> mapColumnConditionToField(it, getArrayFieldConstraintProviders(it.isRequired), this::getFieldTypeForIn))
            .collect(toList()));
    fields.addAll(
        getColumnsOfSearchType(columnToSearchTypeMap, SearchType.NOT_IN)
            .map(sc -> new ColumnConditionMapping(table, sc, getPropertyName(sc), requiredFields.contains(sc)))
            .map(it -> mapColumnConditionToField(it, getArrayFieldConstraintProviders(it.isRequired), this::getFieldTypeForIn))
            .collect(toList()));
    fields.addAll(
        getColumnsOfSearchType(columnToSearchTypeMap, SearchType.BETWEEN)
            .flatMap(
                sc ->
                    Stream.of(
                            new ColumnConditionMapping(table, sc, getPropertyName(sc) + "From", requiredFields.contains(sc)),
                            new ColumnConditionMapping(table, sc, getPropertyName(sc) + "To", requiredFields.contains(sc)))
                        .map(
                            it -> mapColumnConditionToField(it, getCommonConstraintProviders(it.isRequired),
                                this::typeToString)))
            .collect(toList()));
    if (SearchConditionPaginationType.isTypeOffset(searchConditions.getPagination())) {
      fields.addAll(
          of(
              getAuxiliaryField("limit", Integer.class),
              getAuxiliaryField("offset", Integer.class)));
    } else if (SearchConditionPaginationType.isTypePage(searchConditions.getPagination())) {
      fields.addAll(
          of(
              getAuxiliaryField("pageSize", Integer.class),
              getAuxiliaryField("pageNo", Integer.class)));
    }

    return fields;
  }

  private Stream<String> getColumnsOfSearchType(Map<String, SearchType> columnToSearchTypeMap, SearchType searchType) {
    return columnToSearchTypeMap.entrySet()
            .stream().filter(entry -> searchType.equals(entry.getValue()))
            .map(Map.Entry::getKey);
  }

  private Field mapColumnConditionToField(
      ColumnConditionMapping mapping,
      List<ConstraintProvider> providers,
      BiFunction<String, Column, String> conditionToStringTypeMapper) {
    var column = findColumn(mapping.columnName, mapping.table);

    var clazzName = DbTypeConverter.convertToJavaTypeName(column);

    var constraints = providers.stream()
        .flatMap(provide -> provide.getConstraintForProperty(column, clazzName).stream()).collect(toList());

    var field = new Field();
    field.setName(mapping.fieldName);
    field.setType(conditionToStringTypeMapper.apply(clazzName, column));
    field.setConstraints(constraints);
    return field;
  }

  private String getFieldTypeForIn(String clazzName, Column column) {
    return ScopeTypeUtils.getGeneralizedListOfType(typeToString(clazzName, column));
  }

  private Field getAuxiliaryField(String name, Class<?> clazz) {
    var field = new Field();
    field.setName(name);
    field.setType(clazz.getCanonicalName());
    field.setConstraints(emptyList());
    return field;
  }

  private List<ConstraintProvider> getCommonConstraintProviders(boolean isRequired) {
    List<ConstraintProvider> providers = new ArrayList<>();
    providers.add(constraintProviders.getFormattingConstraintProvider());
    providers.add(constraintProviders.getMarshalingConstraintProvider());
    if (isRequired) {
      providers.add((new RequiredConstraintProvider()));
    }
    return providers;
  }

  private List<ConstraintProvider> getArrayFieldConstraintProviders(boolean isRequired) {
    List<ConstraintProvider> providers = getCommonConstraintProviders(isRequired);
    providers.add(new SingleValueAsArrayConstraintProvider());
    return providers;
  }

  @Override
  public String getPath() {
    return "model/src/main/java/model/dto/dto.java.ftl";
  }

  private class ColumnConditionMapping {

    final Table table;
    final String columnName;
    final String fieldName;
    final boolean isRequired;

    ColumnConditionMapping(Table table, String columnName, String fieldName, boolean isRequired) {
      this.table = table;
      this.columnName = columnName;
      this.fieldName = fieldName;
      this.isRequired = isRequired;
    }
  }
}