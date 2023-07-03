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

import com.epam.digital.data.platform.generator.factory.AbstractEntityScopeFactory;
import com.epam.digital.data.platform.generator.metadata.BulkLoadInfoProvider;
import com.epam.digital.data.platform.generator.metadata.EnumProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.model.template.Constraint;
import com.epam.digital.data.platform.generator.model.template.Constraint.Content;
import com.epam.digital.data.platform.generator.model.template.Field;
import com.epam.digital.data.platform.generator.scope.ModelScope;
import com.epam.digital.data.platform.generator.utils.ScopeTypeUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class CreateListEntityScopeFactory extends AbstractEntityScopeFactory<ModelScope> {

  private final BulkLoadInfoProvider bulkLoadInfoProvider;

  public CreateListEntityScopeFactory(
      EnumProvider enumProvider,
      BulkLoadInfoProvider bulkLoadInfoProvider) {
    super(enumProvider);
    this.bulkLoadInfoProvider = bulkLoadInfoProvider;
  }

  @Override
  public List<ModelScope> create(Context context) {
    return bulkLoadInfoProvider.getTablesWithBulkLoad().stream()
        .map(tableName -> findTable(tableName, context))
        .map(
            table -> {
              var scope = new ModelScope();
              var schemaName = getSchemaName(table);
              scope.setClassName(schemaName + "CreateList");

              var field = new Field();
              field.setName("entities");
              field.setType(ScopeTypeUtils.getArrayOfType(schemaName + "Model"));
              field.setConstraints(
                  List.of(
                      new Constraint(
                          "@javax.validation.constraints.NotNull", Collections.emptyList()),
                      new Constraint(
                          "@javax.validation.constraints.Size",
                          Collections.singletonList(new Content("max", "50"))),
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
