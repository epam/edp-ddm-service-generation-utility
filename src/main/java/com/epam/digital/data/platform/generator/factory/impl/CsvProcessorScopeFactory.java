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
import com.epam.digital.data.platform.generator.scope.CsvProcessorScope;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class CsvProcessorScopeFactory extends AbstractScope<CsvProcessorScope> {

  private final BulkLoadInfoProvider bulkLoadInfoProvider;

  public CsvProcessorScopeFactory(
          BulkLoadInfoProvider bulkLoadInfoProvider) {
    this.bulkLoadInfoProvider = bulkLoadInfoProvider;
  }

  @Override
  public List<CsvProcessorScope> create(Context context) {
    return bulkLoadInfoProvider.getTablesWithBulkLoad().stream()
        .map(
            tableName -> {
              var table = findTable(tableName, context);

              var scope = new CsvProcessorScope();
              scope.setClassName(getSchemaName(table) + "CsvProcessor");
              scope.setSchemaName(getSchemaName(table));
              return scope;
            })
        .collect(toList());
  }

  @Override
  public String getPath() {
    return "rest-api/src/main/java/restapi/csv/csvProcessor.java.ftl";
  }
}
