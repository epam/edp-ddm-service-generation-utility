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
import com.epam.digital.data.platform.generator.scope.CreateServiceScope;
import java.util.function.Function;
import org.springframework.stereotype.Component;

import java.util.List;
import schemacrawler.schema.NamedObject;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Component
public class NestedServiceScopeFactory extends AbstractScope<CreateServiceScope> {

  private final NestedStructureProvider nestedStructureProvider;

  public NestedServiceScopeFactory(NestedStructureProvider nestedStructureProvider) {
    this.nestedStructureProvider = nestedStructureProvider;
  }

  @Override
  public List<CreateServiceScope> create(Context context) {
    var tablesMap = context.getCatalog().getTables()
        .stream()
        .collect(toMap(NamedObject::getName, Function.identity()));
    return nestedStructureProvider.findAll().stream()
        .map(
            nestedStructure -> {
              var tableName = nestedStructure.getRoot().getTableName();
              var scope = new CreateServiceScope();
              var schemaName = getSchemaName(nestedStructure.getName(), tableName) + "Nested";
              scope.setClassName(schemaName + "UpsertService");
              scope.setSchemaName(schemaName);
              scope.setPkName(getPkName(tablesMap.get(tableName)));
              scope.setRequestType(
                  "upsert-"
                      + toHyphenTableName(nestedStructure.getName())
                      + "-"
                      + toHyphenTableName(tableName)
                      + "-nested");

              return scope;
            })
        .collect(toList());
  }

  @Override
  public String getPath() {
    return "rest-api/src/main/java/restapi/service/create.java.ftl";
  }
}
