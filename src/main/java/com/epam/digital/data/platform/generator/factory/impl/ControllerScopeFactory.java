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
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.permissionmap.PermissionMap;
import com.epam.digital.data.platform.generator.scope.ControllerScope;
import org.springframework.stereotype.Component;
import schemacrawler.schema.Table;

@Component
public class ControllerScopeFactory extends CrudAbstractScope<ControllerScope> {

  private final PermissionMap permissionMap;

  public ControllerScopeFactory(
      PermissionMap permissionMap) {
    this.permissionMap = permissionMap;
  }

  @Override
  protected ControllerScope map(Table table, Context context) {
    var scope = new ControllerScope();
    scope.setClassName(getSchemaName(table) + "Controller");
    scope.setSchemaName(getSchemaName(table));
    scope.setEndpoint(getEndpoint(table.getName()));
    scope.setPkName(getPkName(table));
    scope.setPkType(getPkTypeName(table));

    var tableName = table.getName();
    scope.setReadRoles(permissionMap.getReadExpressionsFor(tableName));
    scope.setUpdateRoles(permissionMap.getUpdateExpressionsFor(tableName));
    scope.setCreateRoles(permissionMap.getCreateExpressionsFor(tableName));
    scope.setDeleteRoles(permissionMap.getDeleteExpressionsFor(tableName));

    return scope;
  }

  @Override
  public String getPath() {
    return "rest-api/src/main/java/restapi/controller/controller.java.ftl";
  }
}
