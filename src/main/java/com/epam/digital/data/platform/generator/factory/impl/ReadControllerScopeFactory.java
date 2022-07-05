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

import com.epam.digital.data.platform.generator.factory.CrudAbstractScope;
import com.epam.digital.data.platform.generator.metadata.NestedReadEntity;
import com.epam.digital.data.platform.generator.metadata.NestedReadProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.permissionmap.PermissionMap;
import com.epam.digital.data.platform.generator.scope.ReadControllerScope;
import com.google.common.collect.Sets;
import org.springframework.stereotype.Component;
import schemacrawler.schema.Table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class ReadControllerScopeFactory extends CrudAbstractScope<ReadControllerScope> {

  private final PermissionMap permissionMap;
  private final NestedReadProvider nestedReadProvider;

  public ReadControllerScopeFactory(
      PermissionMap permissionMap, NestedReadProvider nestedReadProvider) {
    this.permissionMap = permissionMap;
    this.nestedReadProvider = nestedReadProvider;
  }

  @Override
  protected ReadControllerScope map(Table table, Context context) {
    var scope = new ReadControllerScope();
    scope.setClassName(getSchemaName(table) + "ReadController");
    var nestedEntitiesMap = nestedReadProvider.findFor(table.getName());
    if (nestedEntitiesMap.isEmpty()) {
      scope.setSchemaName(getSchemaName(table));
      scope.setReadRoles(new ArrayList<>(permissionMap.getReadExpressionsFor(table.getName())));
    } else {
      scope.setSchemaName(getSchemaName(table) + "ReadNested");
      var nestedTables =
          nestedEntitiesMap.values().stream()
              .map(NestedReadEntity::getRelatedTable)
              .collect(Collectors.toSet());
      var tablesToCheckPermission =
          Sets.union(Collections.singleton(table.getName()), nestedTables);
      scope.setReadRoles(
          new ArrayList<>(
              permissionMap.getReadExpressionsFor(tablesToCheckPermission)));
    }
    scope.setEndpoint(getEndpoint(table.getName()));
    scope.setPkName(getPkName(table));
    scope.setPkType(getPkTypeName(table));
    scope.setServiceName(getSchemaName(table) + "ReadService");

    return scope;
  }

  @Override
  public String getPath() {
    return "rest-api/src/main/java/restapi/controller/readController.java.ftl";
  }
}
