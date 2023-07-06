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
import com.epam.digital.data.platform.generator.metadata.NestedStructureProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.scope.CsvProcessorScope;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class AsyncDataLoadCsvProcessorKafkaScopeFactory extends
    AbstractAsyncDataLoadScopeFactory<CsvProcessorScope> {

  public AsyncDataLoadCsvProcessorKafkaScopeFactory(
      AsyncDataLoadInfoProvider asyncDataLoadInfoProvider,
      NestedStructureProvider nestedStructureProvider) {
    super(asyncDataLoadInfoProvider, nestedStructureProvider);
  }

  @Override
  public List<CsvProcessorScope> create(Context context) {
    return asyncDataLoadInfoProvider.getTablesWithAsyncLoad().keySet().stream()
        .map(
            tableName -> {
              var schemaName = getSchemaName(tableName, context);

              var scope = new CsvProcessorScope();
              var csvRowSchemaName = schemaName + (isNested(tableName) ? "NestedCsv" : "Model");
              scope.setClassName(csvRowSchemaName + "AsyncDataLoadCsvProcessor");
              scope.setCsvPayloadSchemaName(schemaName + "ModelArrayCsv");
              scope.setCsvRowSchemaName(csvRowSchemaName);
              return scope;
            })
        .collect(toList());
  }

  @Override
  public String getPath() {
    return "kafka-api/src/main/java/kafkaapi/csv/csvProcessor.java.ftl";
  }
}
