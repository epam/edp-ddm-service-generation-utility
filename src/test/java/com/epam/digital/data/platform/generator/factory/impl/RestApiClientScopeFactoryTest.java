package com.epam.digital.data.platform.generator.factory.impl;

import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.getContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.epam.digital.data.platform.generator.metadata.SearchConditionProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.scope.ControllerScope;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RestApiClientScopeFactoryTest {

  @Mock
  private SearchConditionProvider provider;

  private final Context context = getContext();
  private RestApiClientScopeFactory instance;

  @BeforeEach
  void init() {
    instance = new RestApiClientScopeFactory(provider);
  }

  @Test
  void shouldCreateCorrectScopes() {
    when(provider.getExposedSearchConditions()).thenReturn(Set.of("test_schema_search"));
    var expectedSchemaNames = Set.of("TestSchemaSearch", "TestSchemaSearchSearchConditions");

    var restApiClientScopes = instance.create(context);

    ControllerScope resultControllerScope = restApiClientScopes.get(0).getSearchScopes().get(0);

    assertThat(restApiClientScopes).hasSize(1);
    assertThat(restApiClientScopes.get(0).getSchemaNames()).isEqualTo(expectedSchemaNames);
    assertThat(resultControllerScope.getSchemaName()).isEqualTo("TestSchemaSearch");
    assertThat(resultControllerScope.getEndpoint()).isEqualTo("/test-schema-search");
  }

  @Test
  void shouldNotReturnEmptyList() {
    when(provider.getExposedSearchConditions()).thenReturn(Set.of("incorrect_search"));

    var restApiClientScopes = instance.create(context);

    assertThat(restApiClientScopes).isNotEmpty();
  }

}