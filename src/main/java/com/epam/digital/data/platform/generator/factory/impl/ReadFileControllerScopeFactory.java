/*
 * Copyright 2023 EPAM Systems.
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
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.permissionmap.PermissionMap;
import com.epam.digital.data.platform.generator.scope.ReadFileControllerScope;
import com.epam.digital.data.platform.generator.utils.DbUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReadFileControllerScopeFactory extends AbstractScope<ReadFileControllerScope> {

  private final PermissionMap permissionMap;

  public ReadFileControllerScopeFactory(PermissionMap permissionMap) {
    this.permissionMap = permissionMap;
  }

  @Override
  public List<ReadFileControllerScope> create(Context context) {
    return context.getCatalog().getTables().stream()
        .filter(this::isRecentDataTable)
        .flatMap(
            table -> {
              var columns = table.getColumns();
              var fileColumns =
                  columns.stream()
                      .filter(DbUtils::isColumnFileOrFileArray)
                      .collect(Collectors.toList());

              return fileColumns.stream()
                  .map(
                      column -> {
                        var scope = new ReadFileControllerScope();
                        scope.setClassName(
                            getSchemaName(table, column.getName()) + "ReadController");
                        scope.setServiceName(
                            getSchemaName(table, column.getName()) + "ReadService");
                        scope.setPkType(getPkTypeName(table));
                        scope.setEndpoint("/files" + getEndpoint(table.getName()));
                        scope.setColumnEndpoint(toHyphenTableName(column.getName()));
                        scope.setReadRoles(
                            new ArrayList<>(
                                permissionMap.getReadExpressionsForTable(
                                    table.getName(), column.getName())));
                        return scope;
                      });
            })
        .collect(Collectors.toList());
  }

  @Override
  public String getPath() {
    return "rest-api/src/main/java/restapi/controller/readFileController.java.ftl";
  }
}
