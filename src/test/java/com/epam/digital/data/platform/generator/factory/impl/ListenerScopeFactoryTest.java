package com.epam.digital.data.platform.generator.factory.impl;

import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.SCHEMA_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.getContext;
import static org.assertj.core.api.Assertions.assertThat;

import com.epam.digital.data.platform.generator.scope.ListenerDetails;
import com.epam.digital.data.platform.generator.scope.ListenerScope;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ListenerScopeFactoryTest {

  private static final String ROOT_OF_TOPIC_NAME = "test-schema";

  private ListenerScopeFactory instance;

  @BeforeEach
  void setup() {
    instance = new ListenerScopeFactory();
  }

  @Test
  void listenerScopeFactoryTest() {
    String expectedPkType = UUID.class.getCanonicalName();

    List<ListenerScope> resultList = instance.create(getContext());

    assertThat(resultList).hasSize(1);

    ListenerScope resultScope = resultList.get(0);
    assertThat(resultScope.getClassName()).isEqualTo(SCHEMA_NAME + "Listener");
    assertThat(resultScope.getSchemaName()).isEqualTo(SCHEMA_NAME);
    assertThat(resultScope.getPkType()).isEqualTo(expectedPkType);

    assertThat(resultScope.getListeners().get(0)).usingRecursiveComparison().isEqualTo(
        new ListenerDetails("read", ROOT_OF_TOPIC_NAME, expectedPkType, SCHEMA_NAME));
    assertThat(resultScope.getListeners().get(1)).usingRecursiveComparison().isEqualTo(
        new ListenerDetails("update", ROOT_OF_TOPIC_NAME, SCHEMA_NAME, "Void"));
    assertThat(resultScope.getListeners().get(2)).usingRecursiveComparison().isEqualTo(
        new ListenerDetails("create", ROOT_OF_TOPIC_NAME, SCHEMA_NAME,
            "com.epam.digital.data.platform.model.core.kafka.EntityId"));
    assertThat(resultScope.getListeners().get(3)).usingRecursiveComparison().isEqualTo(
        new ListenerDetails("delete", ROOT_OF_TOPIC_NAME, SCHEMA_NAME, "Void"));
  }
}
