/*
 * Copyright 2023 EPAM Systems.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.epam.digital.data.platform.generator.factory.impl.asyncload;

import static java.util.stream.Collectors.toList;

import com.epam.digital.data.platform.generator.constraints.impl.CompositeConstraintProvider;
import com.epam.digital.data.platform.generator.metadata.AsyncDataLoadInfoProvider;
import com.epam.digital.data.platform.generator.metadata.EnumProvider;
import com.epam.digital.data.platform.generator.metadata.NestedNode;
import com.epam.digital.data.platform.generator.metadata.NestedStructureProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.model.template.Constraint;
import com.epam.digital.data.platform.generator.model.template.Constraint.Content;
import com.epam.digital.data.platform.generator.model.template.Field;
import com.epam.digital.data.platform.generator.scope.ModelScope;
import com.epam.digital.data.platform.generator.utils.DbTypeConverter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;
import schemacrawler.schema.Column;

@Component
public class AsyncDataLoadEntityScopeFactory extends
    AbstractAsyncDataLoadEntityScopeFactory<ModelScope> {

  private final CompositeConstraintProvider constraintProviders;

  public AsyncDataLoadEntityScopeFactory(AsyncDataLoadInfoProvider asyncDataLoadInfoProvider,
      CompositeConstraintProvider constraintProviders,
      NestedStructureProvider nestedStructureProvider,
      EnumProvider enumProvider) {
    super(enumProvider, asyncDataLoadInfoProvider, nestedStructureProvider);
    this.constraintProviders = constraintProviders;
  }

  @Override
  public List<ModelScope> create(Context context) {
    var asyncTables = asyncDataLoadInfoProvider.getTablesWithAsyncLoad().keySet();
    var nestedEntities = nestedStructureProvider.findAll().stream()
        .filter(nestedStructure -> asyncTables.contains(nestedStructure.getName()))
        .collect(Collectors.toList());

    var modelScopeMap = new HashMap<String, ModelScope>();

    nestedEntities.stream().flatMap(nestedStructure ->
            createNestedNodeScopeHandlers(nestedStructure.getName(), nestedStructure.getRoot(),
                context))
        .forEach(modelScope -> modelScopeMap.put(modelScope.getClassName(), modelScope));

    return new ArrayList<>(modelScopeMap.values());
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
    scope.setClassName(getSchemaName(structureName, context) + "NestedCsv");
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
                clazzName = getSchemaName(childElement.getTableName()) + "Model";
              } else {
                clazzName =
                    getSchemaName(structureName, childElement.getTableName()) + "NestedCsv";
              }
              var field = new Field();
              field.setName(getPropertyName(childElement.getTableName()));
              field.setType(typeToString(clazzName, column));
              field.setConstraints(Stream.concat(
                      Stream.of(new Constraint("@com.fasterxml.jackson.annotation.JsonUnwrapped",
                          List.of(new Content("prefix", "\"" + childElement.getTableName() + ".\"")))),
                      constraintProviders.getConstraintForProperty(column, clazzName).stream())
                  .collect(Collectors.toList()));
              return field;
            })
        .collect(toList());
  }

  private List<Field> getSimpleFields(List<Column> columns) {
    return columns.stream()
        .map(
            column -> {
              var clazzName = DbTypeConverter.convertToJavaTypeName(column);
              var constraints = constraintProviders.getConstraintForProperty(column, clazzName);

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
