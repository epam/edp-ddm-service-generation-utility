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

import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.ENDPOINT;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.PK_COLUMN_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.SEARCH_SCHEMA_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.TABLE_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.VIEW_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.emptyAsyncData;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.getContext;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.getSettings;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.newCatalog;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withColumn;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withSearchConditionView;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withTable;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withUuidPk;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import com.epam.digital.data.platform.generator.metadata.NestedReadEntity;
import com.epam.digital.data.platform.generator.metadata.NestedReadProvider;
import com.epam.digital.data.platform.generator.metadata.SearchConditionPaginationType;
import com.epam.digital.data.platform.generator.metadata.SearchConditionProvider;
import com.epam.digital.data.platform.generator.metadata.SearchConditions;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.permissionmap.PermissionMap;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.epam.digital.data.platform.generator.scope.SearchControllerScope;
import com.epam.digital.data.platform.generator.utils.ScopeTypeUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SearchControllerScopeFactoryTest {

  private static final String ONE_TO_MANY_COLUMN = "o2m_column";
  private static final String MANY_TO_MANY_COLUMN = "m2m_column";

  private SearchControllerScopeFactory instance;

  @Mock
  SearchConditionProvider searchConditionProvider;
  @Mock
  PermissionMap permissionMap;
  @Mock
  NestedReadProvider nestedReadProvider;

  Context context = getContext();

  @BeforeEach
  void setup() {
    context =
        new Context(
            getSettings(),
            newCatalog(
                withTable(TABLE_NAME, withUuidPk(PK_COLUMN_NAME)),
                withSearchConditionView(
                    VIEW_NAME,
                    withColumn(MANY_TO_MANY_COLUMN, Object.class, "_uuid"),
                    withColumn(ONE_TO_MANY_COLUMN, Object.class, "uuid"))),
            emptyAsyncData());

    instance =
        new SearchControllerScopeFactory(
            searchConditionProvider, permissionMap, nestedReadProvider);

    given(searchConditionProvider.findFor(any())).willReturn(new SearchConditions());
  }

  private void setupReadExpressions(String tableName, String columnName, Set<String> expressions) {
    given(permissionMap.getReadExpressionsForTable(tableName, columnName)).willReturn(expressions);
  }

  private void setupTableColumnMap(String k1, Set<String> v1) {
    given(searchConditionProvider.getTableColumnMapFor(VIEW_NAME)).willReturn(Map.of(k1, v1));
  }

  private void setupTableColumnMap(String k1, Set<String> v1, String k2, Set<String> v2) {
    given(searchConditionProvider.getTableColumnMapFor(VIEW_NAME)).willReturn(Map.of(k1, v1, k2, v2));
  }

  @Test
  void expectControllerScopeIsGenerated() {
    List<SearchControllerScope> scopes = instance.create(context);

    assertThat(scopes).hasSize(1);
    SearchControllerScope resultScope = scopes.get(0);
    assertThat(resultScope.getClassName()).isEqualTo(SEARCH_SCHEMA_NAME + "SearchController");
    assertThat(resultScope.getSchemaName()).isEqualTo(SEARCH_SCHEMA_NAME);
    assertThat(resultScope.getEndpoint()).isEqualTo(ENDPOINT + "-search");
    assertThat(resultScope.getServiceName()).isEqualTo(SEARCH_SCHEMA_NAME + "SearchService");
    assertThat(resultScope.getResponseType()).isEqualTo(List.class.getCanonicalName());
  }

  @Test
  void expectControllerScopeWithPagingResponseIsGenerated() {
    var scInfo = new SearchConditions();
    scInfo.setPagination(SearchConditionPaginationType.PAGE);
    when(searchConditionProvider.findFor(VIEW_NAME)).thenReturn(scInfo);

    List<SearchControllerScope> scopes = instance.create(context);

    assertThat(scopes).hasSize(1);
    SearchControllerScope resultScope = scopes.get(0);
    assertThat(resultScope.getClassName()).isEqualTo(SEARCH_SCHEMA_NAME + "SearchController");
    assertThat(resultScope.getSchemaName()).isEqualTo(SEARCH_SCHEMA_NAME);
    assertThat(resultScope.getEndpoint()).isEqualTo(ENDPOINT + "-search");
    assertThat(resultScope.getServiceName()).isEqualTo(SEARCH_SCHEMA_NAME + "SearchService");
    assertThat(resultScope.getResponseType()).isEqualTo(ScopeTypeUtils.SEARCH_CONDITION_PAGE_TYPE);
  }

  @Test
  void expectControllerScopeForNestedReadIsGenerated() {
    when(nestedReadProvider.findFor(VIEW_NAME))
        .thenReturn(
            Map.of(
                ONE_TO_MANY_COLUMN,
                new NestedReadEntity(VIEW_NAME, ONE_TO_MANY_COLUMN, TABLE_NAME),
                MANY_TO_MANY_COLUMN,
                new NestedReadEntity(VIEW_NAME, MANY_TO_MANY_COLUMN, TABLE_NAME)));
    when(permissionMap.getReadExpressionsForTables(Set.of(TABLE_NAME)))
            .thenReturn(Set.of("role1", "role2"));
    setupTableColumnMap(
            TABLE_NAME, Set.of("field_1")
    );
    setupReadExpressions(TABLE_NAME, "field_1", Set.of("role3"));

    List<SearchControllerScope> scopes = instance.create(context);

    assertThat(scopes).hasSize(1);
    SearchControllerScope resultScope = scopes.get(0);
    assertThat(resultScope.getClassName()).isEqualTo(SEARCH_SCHEMA_NAME + "SearchController");
    assertThat(resultScope.getSchemaName()).isEqualTo(SEARCH_SCHEMA_NAME);
    assertThat(resultScope.getEndpoint()).isEqualTo(ENDPOINT + "-search");
    assertThat(resultScope.getServiceName()).isEqualTo(SEARCH_SCHEMA_NAME + "SearchService");
    assertThat(resultScope.getReadRoles()).containsExactlyInAnyOrder("role1", "role2", "role3");
  }

  @Test
  void shouldSetReadRolesForAllTables() {
    // given
    setupTableColumnMap(
        TABLE_NAME, Set.of("field_1", "field_2"),
        TABLE_NAME + "_2", Set.of("field_3", "field_4")
    );
    setupReadExpressions(TABLE_NAME, "field_1", Set.of("a", "b"));
    setupReadExpressions(TABLE_NAME, "field_2", Set.of("b", "c"));
    setupReadExpressions(TABLE_NAME + "_2", "field_3", Set.of("a", "b"));
    setupReadExpressions(TABLE_NAME + "_2", "field_4", Set.of("b", "c"));
    when(permissionMap.getReadExpressionsForTables(Collections.emptySet()))
            .thenReturn(Collections.emptySet());

    // when
    var roles = instance.create(context).get(0).getReadRoles();

    // then
    assertThat(roles).containsExactlyInAnyOrder("a", "b", "c");
  }
}
