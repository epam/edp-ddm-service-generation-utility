package com.epam.digital.data.platform.generator.factory.impl;

import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.SEARCH_SCHEMA_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.getContext;
import static org.assertj.core.api.Assertions.assertThat;

import com.epam.digital.data.platform.generator.scope.ListenerDetails;
import com.epam.digital.data.platform.generator.scope.ListenerScope;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SearchListenerScopeFactoryTest {

  private static final String ROOT_OF_TOPIC_NAME = "test-schema-search";

  private SearchListenerScopeFactory listenerScopeFactory;

  @BeforeEach
  void setup() {
    listenerScopeFactory = new SearchListenerScopeFactory();
  }

  @Test
  void listenerScopeFactoryTest() {
    List<ListenerScope> resultList = listenerScopeFactory.create(getContext());

    assertThat(resultList).hasSize(1);

    ListenerScope resultScope = resultList.get(0);
    assertThat(resultScope.getClassName()).isEqualTo(SEARCH_SCHEMA_NAME + "Listener");
    assertThat(resultScope.getSchemaName()).isEqualTo(SEARCH_SCHEMA_NAME);

    assertThat(resultScope.getListeners()).hasSize(1);
    assertThat(resultScope.getListeners().get(0)).usingRecursiveComparison().isEqualTo(
        new ListenerDetails("search", ROOT_OF_TOPIC_NAME, SEARCH_SCHEMA_NAME, SEARCH_SCHEMA_NAME));
  }
}
