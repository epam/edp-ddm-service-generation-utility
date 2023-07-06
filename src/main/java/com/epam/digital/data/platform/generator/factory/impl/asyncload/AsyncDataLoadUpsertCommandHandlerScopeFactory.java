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

package com.epam.digital.data.platform.generator.factory.impl.asyncload;

import com.epam.digital.data.platform.generator.metadata.AsyncDataLoadInfoProvider;
import com.epam.digital.data.platform.generator.metadata.NestedStructureProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.scope.CommandHandlerScope;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class AsyncDataLoadUpsertCommandHandlerScopeFactory extends
    AbstractAsyncDataLoadScopeFactory<CommandHandlerScope> {

  public AsyncDataLoadUpsertCommandHandlerScopeFactory(
      AsyncDataLoadInfoProvider asyncDataLoadInfoProvider,
      NestedStructureProvider nestedStructureProvider) {
    super(asyncDataLoadInfoProvider, nestedStructureProvider);
  }

  @Override
  public List<CommandHandlerScope> create(Context context) {
    return asyncDataLoadInfoProvider.getTablesWithAsyncLoad().keySet()
        .stream().filter(Predicate.not(this::isNested))
        .map(tableName -> {
          var scope = new CommandHandlerScope();
          var table = findTable(tableName, context);
          scope.setClassName(getSchemaName(table) + "ModelUpsertCommandHandler");
          scope.setSchemaName(getSchemaName(table));
          scope.setTableDataProviderName(getSchemaName(table) + "TableDataProvider");
          return scope;
        })
        .collect(Collectors.toList());
  }

  @Override
  public String getPath() {
    return "kafka-api/src/main/java/kafkaapi/commandhandler/impl/upsertCommandHandler.java.ftl";
  }
}
