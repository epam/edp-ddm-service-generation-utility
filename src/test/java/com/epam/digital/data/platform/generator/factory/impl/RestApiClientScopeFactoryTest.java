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

import com.epam.digital.data.platform.generator.metadata.ExposeSearchConditionOption;
import com.epam.digital.data.platform.generator.metadata.SearchConditionPaginationType;
import com.epam.digital.data.platform.generator.metadata.SearchConditionProvider;
import com.epam.digital.data.platform.generator.metadata.SearchConditionsBuilder;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.scope.SearchControllerScope;
import com.epam.digital.data.platform.generator.utils.ScopeTypeUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.SEARCH_SCHEMA_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.VIEW_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.getContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestApiClientScopeFactoryTest {

  @Mock private SearchConditionProvider provider;

  private final Context context = getContext();
  private RestApiClientScopeFactory instance;

  @BeforeEach
  void init() {
    instance = new RestApiClientScopeFactory(provider);
  }

  @Test
  void shouldCreateCorrectScopes() {
    when(provider.getExposedSearchConditions(ExposeSearchConditionOption.TREMBITA))
        .thenReturn(Set.of(VIEW_NAME));
    when(provider.findFor(VIEW_NAME)).thenReturn(new SearchConditionsBuilder().build());
    var expectedSchemaNames =
        Set.of("TestSchemaSearchSearchConditionResponse", "TestSchemaSearchSearchConditions");

    var restApiClientScopes = instance.create(context);

    SearchControllerScope resultControllerScope =
        restApiClientScopes.get(0).getSearchScopes().get(0);

    assertThat(restApiClientScopes).hasSize(1);
    assertThat(restApiClientScopes.get(0).getSchemaNames()).isEqualTo(expectedSchemaNames);
    assertThat(resultControllerScope.getSchemaName()).isEqualTo(SEARCH_SCHEMA_NAME);
    assertThat(resultControllerScope.getEndpoint()).isEqualTo("/test-schema-search");
    assertThat(resultControllerScope.getResponseType()).isEqualTo(List.class.getCanonicalName());
  }

  @Test
  void shouldCreateCorrectScopeResponseType() {
    when(provider.getExposedSearchConditions(ExposeSearchConditionOption.TREMBITA))
        .thenReturn(Set.of(VIEW_NAME));
    when(provider.findFor(VIEW_NAME))
        .thenReturn(
            new SearchConditionsBuilder().pagination(SearchConditionPaginationType.PAGE).build());
    var expectedSchemaNames =
        Set.of("TestSchemaSearchSearchConditionResponse", "TestSchemaSearchSearchConditions");

    var restApiClientScopes = instance.create(context);

    SearchControllerScope resultControllerScope =
        restApiClientScopes.get(0).getSearchScopes().get(0);

    assertThat(restApiClientScopes).hasSize(1);
    assertThat(restApiClientScopes.get(0).getSchemaNames()).isEqualTo(expectedSchemaNames);
    assertThat(resultControllerScope.getSchemaName()).isEqualTo(SEARCH_SCHEMA_NAME);
    assertThat(resultControllerScope.getEndpoint()).isEqualTo("/test-schema-search");
    assertThat(resultControllerScope.getResponseType())
        .isEqualTo(ScopeTypeUtils.SEARCH_CONDITION_PAGE_TYPE);
  }

  @Test
  void shouldNotReturnEmptyList() {
    when(provider.getExposedSearchConditions(ExposeSearchConditionOption.TREMBITA))
        .thenReturn(Set.of("incorrect_search"));

    var restApiClientScopes = instance.create(context);

    assertThat(restApiClientScopes).isNotEmpty();
  }
}
