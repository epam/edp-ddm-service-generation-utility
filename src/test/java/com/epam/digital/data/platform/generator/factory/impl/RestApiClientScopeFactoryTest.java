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
import com.epam.digital.data.platform.generator.metadata.SearchConditions;
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

import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.FILE_COLUMN_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.PK_COLUMN_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.SEARCH_SCHEMA_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.TABLE_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.VIEW_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.emptyAsyncData;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.getSettings;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.newCatalog;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withColumn;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withSearchConditionView;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withTable;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withUuidPk;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestApiClientScopeFactoryTest {

  @Mock private SearchConditionProvider provider;

  private Context context;
  private RestApiClientScopeFactory instance;

  @BeforeEach
  void init() {
    context = new Context(
            getSettings(),
            newCatalog(
                    withTable(TABLE_NAME, withUuidPk(PK_COLUMN_NAME),
                            withColumn(FILE_COLUMN_NAME, Object.class, "type_file")),
                    withSearchConditionView(
                            VIEW_NAME,
                            withColumn(FILE_COLUMN_NAME, Object.class, "type_file"))),
            emptyAsyncData());
    instance = new RestApiClientScopeFactory(provider);
  }

  @Test
  void shouldCreateCorrectScopes() {
    when(provider.getExposedSearchConditionsByType(ExposeSearchConditionOption.TREMBITA))
        .thenReturn(Set.of(VIEW_NAME));
    when(provider.findFor(VIEW_NAME)).thenReturn(new SearchConditions());
    var expectedSchemaNames =
        Set.of("TestSchemaSearchSearchConditionResponse", "TestSchemaSearchSearchConditions");

    var actualRestApiClientScopes = instance.create(context);

    var actualResultControllerScope =
        actualRestApiClientScopes.get(0).getSearchScopes().get(0);
    var actualFileEndpointScope =
            actualRestApiClientScopes.get(0).getFileScopes().get(0);

    assertThat(actualRestApiClientScopes).hasSize(1);
    assertThat(actualRestApiClientScopes.get(0).getSearchScopes()).hasSize(1);
    assertThat(actualRestApiClientScopes.get(0).getFileScopes()).hasSize(1);
    assertThat(actualRestApiClientScopes.get(0).getSchemaNames()).isEqualTo(expectedSchemaNames);
    assertThat(actualResultControllerScope.getSchemaName()).isEqualTo(SEARCH_SCHEMA_NAME);
    assertThat(actualResultControllerScope.getEndpoint()).isEqualTo("/test-schema-search");
    assertThat(actualResultControllerScope.getResponseType()).isEqualTo(List.class.getCanonicalName());
    assertThat(actualFileEndpointScope.getTableEndpoint()).isEqualTo("test-schema");
    assertThat(actualFileEndpointScope.getColumnEndpoint()).isEqualTo("column-file");
    assertThat(actualFileEndpointScope.getMethodName()).isEqualTo("getFilesTestSchemaColumnFile");
    assertThat(actualFileEndpointScope.getPkType()).isEqualTo("java.util.UUID");
  }

  @Test
  void shouldCreateCorrectScopeResponseType() {
    when(provider.getExposedSearchConditionsByType(ExposeSearchConditionOption.TREMBITA))
        .thenReturn(Set.of(VIEW_NAME));
    var scInfo = new SearchConditions();
    scInfo.setPagination(SearchConditionPaginationType.PAGE);
    when(provider.findFor(VIEW_NAME)).thenReturn(scInfo);
    var expectedSchemaNames =
        Set.of("TestSchemaSearchSearchConditionResponse", "TestSchemaSearchSearchConditions");

    var actualRestApiClientScopes = instance.create(context);

    var actualResultControllerScope =
        actualRestApiClientScopes.get(0).getSearchScopes().get(0);
    var actualFileEndpointScope =
            actualRestApiClientScopes.get(0).getFileScopes().get(0);

    assertThat(actualRestApiClientScopes).hasSize(1);
    assertThat(actualRestApiClientScopes.get(0).getSearchScopes()).hasSize(1);
    assertThat(actualRestApiClientScopes.get(0).getFileScopes()).hasSize(1);
    assertThat(actualRestApiClientScopes.get(0).getSchemaNames()).isEqualTo(expectedSchemaNames);
    assertThat(actualResultControllerScope.getSchemaName()).isEqualTo(SEARCH_SCHEMA_NAME);
    assertThat(actualResultControllerScope.getEndpoint()).isEqualTo("/test-schema-search");
    assertThat(actualResultControllerScope.getResponseType())
        .isEqualTo(ScopeTypeUtils.SEARCH_CONDITION_PAGE_TYPE);
    assertThat(actualFileEndpointScope.getTableEndpoint()).isEqualTo("test-schema");
    assertThat(actualFileEndpointScope.getColumnEndpoint()).isEqualTo("column-file");
    assertThat(actualFileEndpointScope.getMethodName()).isEqualTo("getFilesTestSchemaColumnFile");
    assertThat(actualFileEndpointScope.getPkType()).isEqualTo("java.util.UUID");
  }

  @Test
  void shouldCreateCorrectScopeWithFileEndpoints() {
    when(provider.getExposedSearchConditionsByType(ExposeSearchConditionOption.TREMBITA))
            .thenReturn(Set.of(VIEW_NAME));
    var scInfo = new SearchConditions();
    scInfo.setPagination(SearchConditionPaginationType.PAGE);
    when(provider.findFor(VIEW_NAME)).thenReturn(scInfo);
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
    when(provider.getExposedSearchConditionsByType(ExposeSearchConditionOption.TREMBITA))
        .thenReturn(Set.of("incorrect_search"));

    var restApiClientScopes = instance.create(context);

    assertThat(restApiClientScopes).isNotEmpty();
  }
}
