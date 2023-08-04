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

import com.epam.digital.data.platform.generator.metadata.SearchConditionPaginationType;
import com.epam.digital.data.platform.generator.metadata.SearchConditionProvider;
import com.epam.digital.data.platform.generator.metadata.SearchConditions;
import com.epam.digital.data.platform.generator.scope.SearchServiceScope;
import com.epam.digital.data.platform.generator.utils.ScopeTypeUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.SEARCH_SCHEMA_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.VIEW_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.getContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchServiceScopeFactoryTest {

  private SearchServiceScopeFactory instance;

  @Mock
  private SearchConditionProvider searchConditionProvider;

  @BeforeEach
  void setup() {
    instance = new SearchServiceScopeFactory(searchConditionProvider);
  }

  @Test
  void successfulTest() {
    when(searchConditionProvider.findFor(VIEW_NAME)).thenReturn(new SearchConditions());

    List<SearchServiceScope> scopes = instance.create(getContext());

    assertThat(scopes).hasSize(1);
    SearchServiceScope scope = scopes.get(0);
    assertThat(scope.getClassName()).isEqualTo(SEARCH_SCHEMA_NAME + "SearchService");
    assertThat(scope.getSchemaName()).isEqualTo(SEARCH_SCHEMA_NAME);
    assertThat(scope.getRequestType()).isEqualTo("search-test-schema-search");
    assertThat(scope.getResponseType()).isEqualTo(List.class.getCanonicalName());
    assertThat(scope.isResponseAsPlainContent()).isTrue();
  }

  @Test
  void expectGenerateScopeForPagingResponse() {
    var scInfo = new SearchConditions();
    scInfo.setPagination(SearchConditionPaginationType.PAGE);
    when(searchConditionProvider.findFor(VIEW_NAME)).thenReturn(scInfo);

    List<SearchServiceScope> scopes = instance.create(getContext());

    assertThat(scopes).hasSize(1);
    SearchServiceScope scope = scopes.get(0);
    assertThat(scope.getResponseType()).isEqualTo(ScopeTypeUtils.SEARCH_CONDITION_PAGE_TYPE);
    assertThat(scope.isResponseAsPlainContent()).isFalse();
  }
}
