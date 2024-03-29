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

import static com.epam.digital.data.platform.generator.utils.ReadOperationUtils.isAsyncSearchCondition;

import com.epam.digital.data.platform.generator.metadata.SearchConditionProvider;
import com.epam.digital.data.platform.generator.model.Context;
import org.springframework.stereotype.Component;
import schemacrawler.schema.Table;

@Component
public class AsyncSearchServiceScopeFactory extends SearchServiceScopeFactory {

  public AsyncSearchServiceScopeFactory(SearchConditionProvider searchConditionProvider) {
    super(searchConditionProvider);
  }

  @Override
  protected boolean isApplicable(Table table, Context context) {
    return isSearchConditionsView(table) &&
        isAsyncSearchCondition(table.getName(), context);
  }

  @Override
  public String getPath() {
    return "rest-api/src/main/java/restapi/service/asyncSearch.java.ftl";
  }
}
