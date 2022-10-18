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
import com.epam.digital.data.platform.generator.scope.ListenerScope;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class CreateListCommandListenerScopeFactory extends AbstractScope<ListenerScope> {

  private final BulkLoadInfoProvider bulkLoadInfoProvider;

  public CreateListCommandListenerScopeFactory(
          BulkLoadInfoProvider bulkLoadInfoProvider) {
    this.bulkLoadInfoProvider = bulkLoadInfoProvider;
  }

  @Override
  public List<ListenerScope> create(Context context) {
    return bulkLoadInfoProvider.getTablesWithBulkLoad().stream()
        .map(
            tableName -> {
              var table = findTable(tableName, context);
              var schemaName = getSchemaName(table) + "CreateList";

              var scope = new ListenerScope();
              scope.setClassName(schemaName + "Listener");
              scope.setSchemaName(schemaName);
              scope.setOperation("create-list");
              scope.setRootOfTopicName(toHyphenTableName(table));
              scope.setOutputType(
                  "java.util.List<com.epam.digital.data.platform.model.core.kafka.EntityId>");
              scope.setHandlerName(schemaName + "CommandHandler");

              return scope;
            })
        .collect(toList());
  }

  @Override
  public String getPath() {
    return "kafka-api/src/main/java/kafkaapi/listener/createListener.java.ftl";
  }
}
