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

import com.epam.digital.data.platform.generator.factory.AbstractScope;
import com.epam.digital.data.platform.generator.metadata.PartialUpdateProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.scope.ListenerScope;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class PartialUpdateListenerScopeFactory extends AbstractScope<ListenerScope> {

  private final PartialUpdateProvider provider;

  public PartialUpdateListenerScopeFactory(
      PartialUpdateProvider provider) {
    this.provider = provider;
  }

  @Override
  public List<ListenerScope> create(Context context) {
    return provider.findAll().stream()
        .map(upd -> {
          var table = findTable(upd.getTableName(), context);
          var schemaName = getSchemaName(table, upd.getName());
          var rootOfTopicName = toHyphenTableName(table) + "-" + toHyphenTableName(upd.getName());

          var scope = new ListenerScope();
          scope.setClassName(schemaName + "Listener");
          scope.setSchemaName(schemaName);
          scope.setPkType(getPkTypeName(table));
          scope.setOperation("update");
          scope.setRootOfTopicName(rootOfTopicName);
          scope.setOutputType("Void");
          scope.setHandlerName(schemaName + "CommandHandler");

          return scope;
        })
        .collect(toList());
  }

  @Override
  public String getPath() {
    return "kafka-api/src/main/java/kafkaapi/listener/listener.java.ftl";
  }
}
