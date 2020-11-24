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

import com.epam.digital.data.platform.generator.factory.AbstractScope;
import com.epam.digital.data.platform.generator.metadata.NestedNode;
import com.epam.digital.data.platform.generator.metadata.NestedStructureProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.model.template.NestedCommandHandlerField;
import com.epam.digital.data.platform.generator.scope.NestedCommandHandlerScope;
import org.springframework.stereotype.Component;
import schemacrawler.schema.Column;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Component
public class NestedCommandHandlerScopeFactory extends AbstractScope<NestedCommandHandlerScope> {

  private final NestedStructureProvider nestedStructureProvider;

  public NestedCommandHandlerScopeFactory(NestedStructureProvider nestedStructureProvider) {
    this.nestedStructureProvider = nestedStructureProvider;
  }

  @Override
  public List<NestedCommandHandlerScope> create(Context context) {
    return nestedStructureProvider.findAll().stream()
        .flatMap(
            nestedStructure ->
                createNestedNodeScopeHandlers(
                    nestedStructure.getName(), nestedStructure.getRoot(), context))
        .collect(toList());
  }

  private Stream<NestedCommandHandlerScope> createNestedNodeScopeHandlers(
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
    var simpleFieldNames =
        table.getColumns().stream()
            .filter(Predicate.not(nestedColumns::contains))
            .map(Column::getName)
            .map(this::getPropertyName)
            .collect(toList());

    var schemaName = getSchemaName(structureName, table.getName()) + "Nested";
    var scope = new NestedCommandHandlerScope();
    scope.setClassName(schemaName + "UpsertCommandHandler");
    scope.setSchemaName(schemaName);
    scope.setRootEntityName(getPropertyName(table.getName()));
    scope.setRootHandler(getPropertyName(node.getTableName()) + "UpsertCommandHandler");
    scope.setSimpleFields(simpleFieldNames);
    scope.getNestedHandlers().addAll(getChildHandlers(structureName, node, nestedColumns));

    var childScopes =
        node.getChildNodes().values().stream()
            .flatMap(childNode -> createNestedNodeScopeHandlers(structureName, childNode, context));
    return Stream.concat(Stream.of(scope), childScopes);
  }

  private List<NestedCommandHandlerField> getChildHandlers(
      String structureName, NestedNode element, List<Column> columns) {
    return columns.stream()
        .map(
            column -> {
              var childElement = element.getChildNodes().get(column.getName());

              String childSchemaName = childElement.getChildNodes().isEmpty() ?
                      getPropertyName(childElement.getTableName()) :
                      getPropertyName(structureName, childElement.getTableName()) + "Nested";

              var nestedCommandHandlerField = new NestedCommandHandlerField();
              nestedCommandHandlerField.setName(childSchemaName + "UpsertCommandHandler");
              nestedCommandHandlerField.setInjectionField(
                  getPropertyName(column.getName()));
              nestedCommandHandlerField.setChildField(getPropertyName(childElement.getTableName()));
              return nestedCommandHandlerField;
            })
        .collect(toList());
  }

  @Override
  public String getPath() {
    return "kafka-api/src/main/java/kafkaapi/commandhandler/impl/nestedCommandHandler.java.ftl";
  }
}
