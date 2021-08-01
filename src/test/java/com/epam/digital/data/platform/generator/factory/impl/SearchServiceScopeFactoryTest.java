package com.epam.digital.data.platform.generator.factory.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.SEARCH_SCHEMA_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.getContext;

import com.epam.digital.data.platform.generator.scope.ServiceScope;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SearchServiceScopeFactoryTest {

  private SearchServiceScopeFactory instance;

  @BeforeEach
  void setup() {
    instance = new SearchServiceScopeFactory();
  }

  @Test
  void successfulTest() {
    List<ServiceScope> scopes = instance.create(getContext());

    assertThat(scopes).hasSize(1);
    ServiceScope scope = scopes.get(0);
    assertThat(scope.getClassName()).isEqualTo(SEARCH_SCHEMA_NAME + "SearchService");
    assertThat(scope.getSchemaName()).isEqualTo(SEARCH_SCHEMA_NAME);
    assertThat(scope.getRequestType()).isEqualTo("search-test-schema-search");
  }
}
