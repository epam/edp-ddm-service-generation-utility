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
import com.epam.digital.data.platform.generator.scope.CommandHandlerScope;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class CreateListCommandHandlerScopeFactory extends AbstractScope<CommandHandlerScope> {

  private final BulkLoadInfoProvider bulkLoadInfoProvider;

  public CreateListCommandHandlerScopeFactory(BulkLoadInfoProvider bulkLoadInfoProvider) {
    this.bulkLoadInfoProvider = bulkLoadInfoProvider;
  }

  @Override
  public List<CommandHandlerScope> create(Context context) {
    return bulkLoadInfoProvider.getTablesWithBulkLoad().stream()
        .map(
            tableName -> {
              var table = findTable(tableName, context);
              String modelName = getSchemaName(table);

              var scope = new CommandHandlerScope();

              scope.setClassName(modelName + "CreateListCommandHandler");
              scope.setSchemaName(modelName);
              return scope;
            })
        .collect(toList());
  }

  @Override
  public String getPath() {
    return "kafka-api/src/main/java/kafkaapi/commandhandler/impl/createListCommandHandler.java.ftl";
  }
}
