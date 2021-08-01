package com.epam.digital.data.platform.generator.factory.impl;

import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.ENDPOINT;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.SEARCH_SCHEMA_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.TABLE_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.VIEW_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.getContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.epam.digital.data.platform.generator.metadata.SearchConditionProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.permissionmap.PermissionMap;
import com.epam.digital.data.platform.generator.scope.ControllerScope;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SearchControllerScopeFactoryTest {

  private SearchControllerScopeFactory instance;

  @Mock
  SearchConditionProvider searchConditionProvider;

  @Mock
  PermissionMap permissionMap;

  Context context = getContext();

  @BeforeEach
  void setup() {
    instance = new SearchControllerScopeFactory(searchConditionProvider, permissionMap);
  }

  private void setupReadExpressions(String tableName, String columnName, List<String> expressions) {
    given(permissionMap.getReadExpressionsFor(tableName, columnName)).willReturn(expressions);
  }

  private void setupTableColumnMap(String k1, Set<String> v1) {
    given(searchConditionProvider.getTableColumnMapFor(VIEW_NAME)).willReturn(Map.of(k1, v1));
  }

  private void setupTableColumnMap(String k1, Set<String> v1, String k2, Set<String> v2) {
    given(searchConditionProvider.getTableColumnMapFor(VIEW_NAME)).willReturn(Map.of(k1, v1, k2, v2));
  }

  @Test
  void successfulTest() {
    List<ControllerScope> scopes = instance.create(context);

    assertThat(scopes).hasSize(1);
    ControllerScope resultScope = scopes.get(0);
    assertThat(resultScope.getClassName()).isEqualTo(SEARCH_SCHEMA_NAME + "SearchController");
    assertThat(resultScope.getSchemaName()).isEqualTo(SEARCH_SCHEMA_NAME);
    assertThat(resultScope.getEndpoint()).isEqualTo(ENDPOINT + "-search");
  }

  @Test
  void shouldSetReadRolesForAllTables() {
    // given
    setupTableColumnMap(
        TABLE_NAME, Set.of("field_1", "field_2"),
        TABLE_NAME + "_2", Set.of("field_3", "field_4")
    );
    setupReadExpressions(TABLE_NAME, "field_1", List.of("a", "b"));
    setupReadExpressions(TABLE_NAME, "field_2", List.of("b", "c"));
    setupReadExpressions(TABLE_NAME + "_2", "field_3", List.of("a", "b"));
    setupReadExpressions(TABLE_NAME + "_2", "field_4", List.of("b", "c"));

    // when
    var roles = instance.create(context).get(0).getReadRoles();

    // then
    assertThat(roles).containsExactlyInAnyOrder("a", "b", "c");
  }
}
