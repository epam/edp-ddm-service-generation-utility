/*
 * Copyright 2022 EPAM Systems.
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

import com.epam.digital.data.platform.generator.factory.AbstractScope;
import com.epam.digital.data.platform.generator.metadata.BulkLoadInfoProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.permissionmap.PermissionMap;
import com.epam.digital.data.platform.generator.scope.ModifyControllerScope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BulkLoadControllerScopeFactory extends AbstractScope<ModifyControllerScope> {

  private final PermissionMap permissionMap;

  private final BulkLoadInfoProvider bulkLoadInfoProvider;

  public BulkLoadControllerScopeFactory(
      PermissionMap permissionMap, BulkLoadInfoProvider bulkLoadInfoProvider) {
    this.permissionMap = permissionMap;
    this.bulkLoadInfoProvider = bulkLoadInfoProvider;
  }

  @Override
  public List<ModifyControllerScope> create(Context context) {
    return bulkLoadInfoProvider.getTablesWithBulkLoad()
            .stream()
            .map(tableName -> {
              var table = findTable(tableName, context);

              var scope = new ModifyControllerScope();
              scope.setClassName(getSchemaName(table) + "BulkLoadController");
              scope.setSchemaName(getSchemaName(table));
              scope.setEndpoint(getEndpoint(table.getName()));

              scope.setCreateRoles(new ArrayList<>(permissionMap.getCreateExpressionsFor(tableName)));
              return scope;
            })
            .collect(Collectors.toList());
  }

  @Override
  public String getPath() {
    return "rest-api/src/main/java/restapi/controller/bulkLoadController.java.ftl";
  }
}
