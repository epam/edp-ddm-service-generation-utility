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

import static java.util.stream.Collectors.toList;

import com.epam.digital.data.platform.generator.metadata.AsyncDataLoadInfoProvider;
import com.epam.digital.data.platform.generator.metadata.EnumProvider;
import com.epam.digital.data.platform.generator.metadata.NestedStructureProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.model.template.Constraint;
import com.epam.digital.data.platform.generator.model.template.Constraint.Content;
import com.epam.digital.data.platform.generator.model.template.Field;
import com.epam.digital.data.platform.generator.scope.ModelScope;
import com.epam.digital.data.platform.generator.utils.ScopeTypeUtils;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class KafkaApiArrayEntityScopeFactory extends
    AbstractAsyncDataLoadEntityScopeFactory<ModelScope> {

  public KafkaApiArrayEntityScopeFactory(
      EnumProvider enumProvider,
      AsyncDataLoadInfoProvider asyncDataLoadInfoProvider,
      NestedStructureProvider nestedStructureProvider) {
    super(enumProvider, asyncDataLoadInfoProvider, nestedStructureProvider);
  }

  @Override
  public List<ModelScope> create(Context context) {
    return asyncDataLoadInfoProvider.getTablesWithAsyncLoad().entrySet().stream()
        .map(
            tableEntry -> {
              var scope = new ModelScope();
              var schemaName = getSchemaName(tableEntry.getKey(), context);
              scope.setClassName(schemaName + "ModelArrayCsv");

              var field = new Field();
              field.setName("entities");
              field.setType(
                  ScopeTypeUtils.getArrayOfType(schemaName + getSchemaSuffix(tableEntry.getKey())));
              String maxSize = tableEntry.getValue();
              field.setConstraints(
                  List.of(
                      new Constraint(
                          "@javax.validation.constraints.NotNull", Collections.emptyList()),
                      new Constraint(
                          "@javax.validation.constraints.Size",
                          List.of(new Content("max", maxSize),
                              new Content("message", "\"Maximum list size - " + maxSize + "\""))),
                      new Constraint("@javax.validation.Valid", Collections.emptyList())));
              scope.getFields().add(field);
              return scope;
            })
        .collect(toList());
  }

  @Override
  public String getPath() {
    return "model/src/main/java/model/dto/dto.java.ftl";
  }
}
