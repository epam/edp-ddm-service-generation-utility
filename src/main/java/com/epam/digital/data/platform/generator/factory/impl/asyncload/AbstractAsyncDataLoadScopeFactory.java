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

import com.epam.digital.data.platform.generator.factory.AbstractScope;
import com.epam.digital.data.platform.generator.metadata.AsyncDataLoadInfoProvider;
import com.epam.digital.data.platform.generator.metadata.NestedStructure;
import com.epam.digital.data.platform.generator.metadata.NestedStructureProvider;
import com.epam.digital.data.platform.generator.model.Context;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractAsyncDataLoadScopeFactory<T> extends AbstractScope<T> {

  protected final AsyncDataLoadInfoProvider asyncDataLoadInfoProvider;
  protected final NestedStructureProvider nestedStructureProvider;

  protected final Map<String, NestedStructure> nestedStructureMap;

  protected AbstractAsyncDataLoadScopeFactory(AsyncDataLoadInfoProvider asyncDataLoadInfoProvider,
      NestedStructureProvider nestedStructureProvider) {
    this.asyncDataLoadInfoProvider = asyncDataLoadInfoProvider;
    this.nestedStructureProvider = nestedStructureProvider;

    this.nestedStructureMap = nestedStructureProvider.findAll()
        .stream().collect(Collectors.toMap(NestedStructure::getName, Function.identity()));
  }

  protected String getSchemaName(String entityName, Context context) {
    if (isNested(entityName)) {
      var table = findTable(nestedStructureMap.get(entityName).getRoot().getTableName(),
          context);
      return getSchemaName(entityName, table.getName());
    } else {
      var table = findTable(entityName, context);
      return getSchemaName(table);
    }
  }

  protected String getEndpoint(String entityName) {
    return isNested(entityName)
        ? super.getEndpoint("nested/" + entityName)
        : super.getEndpoint(entityName);
  }

  protected boolean isNested(String entityName) {
    return nestedStructureMap.containsKey(entityName);
  }
}
