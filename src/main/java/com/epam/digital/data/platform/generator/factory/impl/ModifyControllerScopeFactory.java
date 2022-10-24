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
import com.epam.digital.data.platform.generator.scope.ModifyControllerScope;
import org.springframework.stereotype.Component;
import schemacrawler.schema.Table;

import java.util.ArrayList;

@Component
public class ModifyControllerScopeFactory extends CrudAbstractScope<ModifyControllerScope> {

  private final PermissionMap permissionMap;

  public ModifyControllerScopeFactory(
      PermissionMap permissionMap) {
    this.permissionMap = permissionMap;
  }

  @Override
  protected ModifyControllerScope map(Table table, Context context) {
    var scope = new ModifyControllerScope();
    scope.setClassName(getSchemaName(table) + "ModifyController");
    scope.setSchemaName(getSchemaName(table));
    scope.setEndpoint(getEndpoint(table.getName()));
    scope.setPkName(getPkName(table));
    scope.setPkType(getPkTypeName(table));

    var tableName = table.getName();
    scope.setUpdateRoles(new ArrayList<>(permissionMap.getUpdateExpressionsFor(tableName)));
    scope.setCreateRoles(new ArrayList<>(permissionMap.getCreateExpressionsFor(tableName)));
    scope.setDeleteRoles(new ArrayList<>(permissionMap.getDeleteExpressionsFor(tableName)));

    return scope;
  }

  @Override
  public String getPath() {
    return "rest-api/src/main/java/restapi/controller/modifyController.java.ftl";
  }
}
