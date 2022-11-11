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

package com.epam.digital.data.platform.generator.factory.impl.nested;

import com.epam.digital.data.platform.generator.constraints.impl.CompositeConstraintProvider;
import com.epam.digital.data.platform.generator.factory.AbstractEntityScopeFactory;
import com.epam.digital.data.platform.generator.metadata.EnumProvider;
import com.epam.digital.data.platform.generator.metadata.NestedNode;
import com.epam.digital.data.platform.generator.metadata.NestedStructureProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.model.template.Field;
import com.epam.digital.data.platform.generator.scope.ModelScope;
import com.epam.digital.data.platform.generator.utils.DbTypeConverter;
import org.springframework.stereotype.Component;
import schemacrawler.schema.Column;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Component
public class NestedEntityScopeFactory extends AbstractEntityScopeFactory<ModelScope> {

  private final CompositeConstraintProvider constraintProviders;
  private final NestedStructureProvider nestedStructureProvider;

  public NestedEntityScopeFactory(
      EnumProvider enumProvider, CompositeConstraintProvider constraintProviders,
      NestedStructureProvider nestedStructureProvider) {
    super(enumProvider);
    this.constraintProviders = constraintProviders;
    this.nestedStructureProvider = nestedStructureProvider;
  }

  @Override
  public List<ModelScope> create(Context context) {
    return nestedStructureProvider.findAll().stream()
        .flatMap(
            nestedStructure ->
                    createNestedNodeScopeHandlers(
                    nestedStructure.getName(), nestedStructure.getRoot(), context))
        .collect(toList());
  }

  private Stream<ModelScope> createNestedNodeScopeHandlers(
      String structureName, NestedNode node, Context context) {
    if (node.getChildNodes().entrySet().isEmpty()) {
      return Stream.empty();
    }
    var table = findTable(node.getTableName(), context);
    var nestedColumnNames = node.getChildNodes().keySet();
    var nestedColumns =
        table.getColumns().stream()
            .filter(column -> nestedColumnNames.contains(column.getName()))
            .collect(toList());
    var simpleColumns =
        table.getColumns().stream()
            .filter(Predicate.not(nestedColumns::contains))
            .collect(toList());

    var scope = new ModelScope();
    scope.setClassName(getSchemaName(structureName, table.getName()) + "Nested");
    scope.getFields().addAll(getSimpleFields(simpleColumns));
    scope.getFields().addAll(getNestedFields(structureName, node, nestedColumns));

    var childScopes =
        node.getChildNodes().values().stream()
            .flatMap(childNode -> createNestedNodeScopeHandlers(structureName, childNode, context));
    return Stream.concat(Stream.of(scope), childScopes);
  }

  private List<Field> getNestedFields(
      String structureName, NestedNode element, List<Column> columns) {
    return columns.stream()
        .map(
            column -> {
              var childElement = element.getChildNodes().get(column.getName());

              String clazzName;
              if (childElement.getChildNodes().isEmpty()) {
                clazzName = getSchemaName(childElement.getTableName());
              } else {
                clazzName = getSchemaName(structureName, childElement.getTableName()) + "Nested";
              }
              var field = new Field();
              field.setName(getPropertyName(childElement.getTableName()));
              field.setType(typeToString(clazzName, column));
              field.setConstraints(
                  constraintProviders
                      .getConstraintForProperty(column, clazzName));
              return field;
            })
        .collect(toList());
  }

  private List<Field> getSimpleFields(List<Column> columns) {
    return columns.stream()
        .map(
            column -> {
              var clazzName = DbTypeConverter.convertToJavaTypeName(column);

              var constraints =
                  constraintProviders.getConstraintForProperty(
                      column, clazzName);

              var field = new Field();
              field.setName(getPropertyName(column));
              field.setType(typeToString(clazzName, column));
              field.setConstraints(constraints);
              return field;
            })
        .collect(toList());
  }

  @Override
  public String getPath() {
    return "model/src/main/java/model/dto/dto.java.ftl";
  }
}
