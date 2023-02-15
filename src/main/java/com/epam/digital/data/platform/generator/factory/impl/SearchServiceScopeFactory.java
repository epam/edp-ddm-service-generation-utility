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

import com.epam.digital.data.platform.generator.metadata.SearchConditionPaginationType;
import com.epam.digital.data.platform.generator.metadata.SearchConditionProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.utils.ScopeTypeUtils;
import org.springframework.stereotype.Component;
import schemacrawler.schema.Table;
import com.epam.digital.data.platform.generator.factory.SearchConditionsAbstractScope;
import com.epam.digital.data.platform.generator.scope.SearchServiceScope;

import java.util.List;

@Component
public class SearchServiceScopeFactory extends SearchConditionsAbstractScope<SearchServiceScope> {

  private final SearchConditionProvider searchConditionProvider;

  public SearchServiceScopeFactory(SearchConditionProvider searchConditionProvider) {
    this.searchConditionProvider = searchConditionProvider;
  }

  @Override
  protected SearchServiceScope map(Table table, Context context) {
    var searchConditionInfo = searchConditionProvider.findFor(getCutTableName(table.getName()));
    var serviceScope = new SearchServiceScope();
    serviceScope.setClassName(getSchemaName(table) + "SearchService");
    serviceScope.setSchemaName(getSchemaName(table));
    serviceScope.setRequestType("search" + "-" + toHyphenTableName(table.getName()));
    if (SearchConditionPaginationType.isTypePage(searchConditionInfo.getPagination())) {
      serviceScope.setResponseType(ScopeTypeUtils.SEARCH_CONDITION_PAGE_TYPE);
      serviceScope.setResponseAsPlainContent(false);
    } else {
      serviceScope.setResponseType(List.class.getCanonicalName());
      serviceScope.setResponseAsPlainContent(true);
    }
    return serviceScope;
  }

  @Override
  protected boolean isApplicable(Table table, Context context) {
    return super.isApplicable(table, context) &&
        !isAsyncSearchCondition(table.getName(), context);
  }

  @Override
  public String getPath() {
    return "rest-api/src/main/java/restapi/service/search.java.ftl";
  }
}
