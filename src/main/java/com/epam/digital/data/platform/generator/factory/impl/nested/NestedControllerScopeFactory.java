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
import com.epam.digital.data.platform.generator.metadata.NestedStructure;
import com.epam.digital.data.platform.generator.metadata.NestedStructureProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.permissionmap.PermissionMap;
import com.epam.digital.data.platform.generator.scope.ModifyControllerScope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Component
public class NestedControllerScopeFactory extends AbstractScope<ModifyControllerScope> {

  private final NestedStructureProvider nestedStructureProvider;
  private final PermissionMap permissionMap;

  public NestedControllerScopeFactory(
      NestedStructureProvider nestedStructureProvider, PermissionMap permissionMap) {
    this.nestedStructureProvider = nestedStructureProvider;
    this.permissionMap = permissionMap;
  }

  @Override
  public List<ModifyControllerScope> create(Context context) {
    return nestedStructureProvider.findAll().stream()
        .map(
            nestedStructure -> {
              var tableName = nestedStructure.getRoot().getTableName();
              var schemaName = getSchemaName(nestedStructure.getName(), tableName) + "Nested";
              var scope = new ModifyControllerScope();
              scope.setClassName(schemaName + "Controller");
              scope.setSchemaName(schemaName);
              scope.setEndpoint(getEndpoint(nestedStructure.getName()));
              scope.setCreateRoles(new ArrayList<>(findRolesFor(nestedStructure)));
              return scope;
            })
        .collect(toList());
  }

  private Set<String> findRolesFor(NestedStructure nestedStructure) {
    var rootNode = nestedStructure.getRoot();
    var allTables = getNestedTables(rootNode).collect(toSet());

    return permissionMap.getCreateExpressionsFor(allTables);
  }

  private Stream<String> getNestedTables(NestedNode node) {
    if (node.getChildNodes().isEmpty()) {
      return Stream.of(node.getTableName());
    }
    var childTables = node.getChildNodes().values().stream().flatMap(this::getNestedTables);
    return Stream.concat(Stream.of(node.getTableName()), childTables);
  }

  @Override
  public String getPath() {
    return "rest-api/src/main/java/restapi/controller/nestedController.java.ftl";
  }
}
