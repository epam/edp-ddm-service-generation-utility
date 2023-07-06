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

import com.epam.digital.data.platform.generator.metadata.AsyncDataLoadInfoProvider;
import com.epam.digital.data.platform.generator.metadata.NestedStructureProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.scope.AsyncDataLoadListenerScope;
import java.util.HashMap;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class AsyncDataLoadListenerScopeFactory extends
    AbstractAsyncDataLoadScopeFactory<AsyncDataLoadListenerScope> {

  public AsyncDataLoadListenerScopeFactory(
      AsyncDataLoadInfoProvider asyncDataLoadInfoProvider,
      NestedStructureProvider nestedStructureProvider) {
    super(asyncDataLoadInfoProvider, nestedStructureProvider);
  }

  @Override
  public List<AsyncDataLoadListenerScope> create(Context context) {
    var scope = new AsyncDataLoadListenerScope();
    scope.setClassName("AsyncDataLoadListener");
    scope.setOperation("upsert");
    scope.setFilterName("asyncDataLoadListenerFilterStrategy");

    var entityNamesToSchemaNames = new HashMap<String, String>();
    asyncDataLoadInfoProvider.getTablesWithAsyncLoad().keySet().forEach(entityName -> {
      var schemaName = getSchemaName(entityName, context);
      if (isNested(entityName)) {
        entityNamesToSchemaNames.put(getSchemaName("nested/" + entityName),
            schemaName + "NestedCsv");
      } else {
        entityNamesToSchemaNames.put(getSchemaName(entityName), schemaName + "Model");
      }
    });
    scope.setEntityNamesToSchemaNames(entityNamesToSchemaNames);

    return List.of(scope);
  }

  @Override
  public String getPath() {
    return "kafka-api/src/main/java/kafkaapi/listener/asyncload/asyncLoadListener.java.ftl";
  }
}
