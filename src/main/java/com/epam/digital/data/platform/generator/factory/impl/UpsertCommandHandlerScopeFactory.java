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

import com.epam.digital.data.platform.generator.factory.AbstractScope;
import com.epam.digital.data.platform.generator.metadata.NestedNode;
import com.epam.digital.data.platform.generator.metadata.NestedStructureProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.scope.CommandHandlerScope;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;

@Component
public class UpsertCommandHandlerScopeFactory extends AbstractScope<CommandHandlerScope> {


  private final NestedStructureProvider nestedStructureProvider;

  public UpsertCommandHandlerScopeFactory(NestedStructureProvider nestedStructureProvider) {
    this.nestedStructureProvider = nestedStructureProvider;
  }

  @Override
  public List<CommandHandlerScope> create(Context context) {
    return nestedStructureProvider.findAll().stream()
        .flatMap(
            nestedStructure ->
                createCommandHandlerScopes(nestedStructure.getRoot(), context))
        .collect(toList());
  }

  private Stream<CommandHandlerScope> createCommandHandlerScopes(NestedNode node, Context context) {
    if (node == null || node.getChildNodes().isEmpty()) {
      return Stream.empty();
    }
    return getTableNames(node, new HashSet<>()).stream()
        .map(
            tableName -> {
              var scope = new CommandHandlerScope();
              var table = findTable(tableName, context);
              scope.setClassName(getSchemaName(table) + "UpsertCommandHandler");
              scope.setSchemaName(getSchemaName(table));
              scope.setTableDataProviderName(getSchemaName(table) + "TableDataProvider");
              return scope;
            });
  }

  private Set<String> getTableNames(NestedNode node, Set<String> names) {
    names.add(node.getTableName());
    if (!node.getChildNodes().isEmpty()) {
      node.getChildNodes().forEach((key, value) -> getTableNames(value, names));
    }
    return names;
  }

  @Override
  public String getPath() {
    return "kafka-api/src/main/java/kafkaapi/commandhandler/impl/upsertCommandHandler.java.ftl";
  }
}
