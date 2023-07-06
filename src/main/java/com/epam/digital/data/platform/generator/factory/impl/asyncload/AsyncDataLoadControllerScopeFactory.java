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

import com.epam.digital.data.platform.generator.metadata.AsyncDataLoadInfoProvider;
import com.epam.digital.data.platform.generator.metadata.NestedStructureProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.permissionmap.PermissionMap;
import com.epam.digital.data.platform.generator.scope.ModifyControllerScope;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class AsyncDataLoadControllerScopeFactory extends
    AbstractAsyncDataLoadScopeFactory<ModifyControllerScope> {

  private final PermissionMap permissionMap;

  public AsyncDataLoadControllerScopeFactory(
      PermissionMap permissionMap, AsyncDataLoadInfoProvider asyncDataLoadInfoProvider,
      NestedStructureProvider nestedStructureProvider) {
    super(asyncDataLoadInfoProvider, nestedStructureProvider);
    this.permissionMap = permissionMap;
  }

  @Override
  public List<ModifyControllerScope> create(Context context) {
    return asyncDataLoadInfoProvider.getTablesWithAsyncLoad().keySet().stream()
        .map(tableName -> {
          var schemaName = getSchemaName(tableName, context);

          var scope = new ModifyControllerScope();
          scope.setClassName(schemaName + "AsyncDataLoadController");
          scope.setSchemaName(schemaName);
          scope.setEndpoint("/v2" + getEndpoint(tableName));

          scope.setCreateRoles(new ArrayList<>(permissionMap.getCreateExpressionsFor(tableName)));
          return scope;
        })
        .collect(Collectors.toList());
  }

  @Override
  public String getPath() {
    return "rest-api/src/main/java/restapi/controller/asyncDataLoadController.java.ftl";
  }
}
