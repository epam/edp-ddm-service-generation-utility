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

import com.epam.digital.data.platform.generator.factory.AbstractScope;
import com.epam.digital.data.platform.generator.metadata.PartialUpdateProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.scope.CommandHandlerScope;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PartialUpdateCommandHandlerScopeFactory extends AbstractScope<CommandHandlerScope> {

  private final PartialUpdateProvider provider;

  public PartialUpdateCommandHandlerScopeFactory(
      PartialUpdateProvider provider) {
    this.provider = provider;
  }

  @Override
  public List<CommandHandlerScope> create(Context context) {
    return provider.findAll().stream()
        .map(upd -> {
          var table = findTable(upd.getTableName(), context);

          var scope = new CommandHandlerScope();
          scope.setClassName(getSchemaName(table, upd.getName()) + "CommandHandler");
          scope.setSchemaName(getSchemaName(table, upd.getName()));
          scope.setTableDataProviderName(getSchemaName(table) + "TableDataProvider");
          scope.setOperation("update");

          return scope;
        })
        .collect(toList());
  }

  @Override
  public String getPath() {
    return "kafka-api/src/main/java/kafkaapi/commandhandler/impl/commandHandler.java.ftl";
  }
}
