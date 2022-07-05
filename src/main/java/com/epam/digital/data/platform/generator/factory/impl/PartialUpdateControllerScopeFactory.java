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
import static java.util.stream.Collectors.toSet;

import com.epam.digital.data.platform.generator.factory.AbstractScope;
import com.epam.digital.data.platform.generator.metadata.PartialUpdate;
import com.epam.digital.data.platform.generator.metadata.PartialUpdateProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.permissionmap.PermissionMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import com.epam.digital.data.platform.generator.scope.ModifyControllerScope;
import org.springframework.stereotype.Component;
import schemacrawler.schema.Table;

@Component
public class PartialUpdateControllerScopeFactory extends AbstractScope<ModifyControllerScope> {

  private final PartialUpdateProvider partialUpdateProvider;
  private final PermissionMap permissionMap;

  public PartialUpdateControllerScopeFactory(
      PartialUpdateProvider partialUpdateProvider,
      PermissionMap permissionMap) {
    this.partialUpdateProvider = partialUpdateProvider;
    this.permissionMap = permissionMap;
  }

  @Override
  public List<ModifyControllerScope> create(Context context) {
    return partialUpdateProvider.findAll().stream()
        .map(upd -> {
          var table = findTable(upd.getTableName(), context);

          var scope = new ModifyControllerScope();
          scope.setClassName(getSchemaName(table, upd.getName()) + "Controller");
          scope.setSchemaName(getSchemaName(table, upd.getName()));
          scope.setEndpoint("/partial" + getEndpoint(upd.getName()));
          scope.setPkName(getPkName(table));
          scope.setPkType(getPkTypeName(table));

          scope.setUpdateRoles(new ArrayList<>(findRolesFor(upd, table)));

          return scope;
        })
        .collect(toList());
  }

  private Set<String> findRolesFor(PartialUpdate update, Table table) {
    Function<String, Set<String>> getUpdateExpressionsForColumn =
        column -> permissionMap.getUpdateExpressionsFor(table.getName(), column);

    return update.getColumns().stream()
        .map(getUpdateExpressionsForColumn)
        .flatMap(Collection::stream)
        .collect(toSet());
  }

  @Override
  public String getPath() {
    return "rest-api/src/main/java/restapi/controller/updateController.java.ftl";
  }
}
