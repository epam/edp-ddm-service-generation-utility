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

package com.epam.digital.data.platform.generator.factory.impl.nested;

import com.epam.digital.data.platform.generator.factory.AbstractScope;
import com.epam.digital.data.platform.generator.metadata.NestedStructureProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.scope.CommandListenerScope;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class NestedListenerScopeFactory extends AbstractScope<CommandListenerScope> {

  static final String CREATE_OUTPUT_TYPE =
      "com.epam.digital.data.platform.model.core.kafka.EntityId";

  private final NestedStructureProvider nestedStructureProvider;

  public NestedListenerScopeFactory(NestedStructureProvider nestedStructureProvider) {
    this.nestedStructureProvider = nestedStructureProvider;
  }

  @Override
  public List<CommandListenerScope> create(Context context) {
    return nestedStructureProvider.findAll().stream()
        .map(
            nestedStructure -> {
              var tableName = nestedStructure.getRoot().getTableName();
              var schemaName = getSchemaName(nestedStructure.getName(), tableName) + "Nested";
              var scope = new CommandListenerScope();
              scope.setClassName(schemaName + "CreateListener");
              scope.setSchemaName(schemaName);

              var rootOfTopicName =
                  toHyphenTableName(nestedStructure.getName())
                      + "-"
                      + toHyphenTableName(nestedStructure.getRoot().getTableName())
                      + "-nested";
              scope.setRootOfTopicName(rootOfTopicName);
              scope.setOperation("create");
              scope.setOutputType(CREATE_OUTPUT_TYPE);
              scope.setCommandHandler(schemaName + "CreateCommandHandler");

              return scope;
            })
        .collect(toList());
  }

  @Override
  public String getPath() {
    return "kafka-api/src/main/java/kafkaapi/listener/listener.java.ftl";
  }
}
