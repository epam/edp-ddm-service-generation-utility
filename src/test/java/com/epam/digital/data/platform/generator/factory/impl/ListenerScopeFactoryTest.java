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
        new ListenerDetails("update", ROOT_OF_TOPIC_NAME, SCHEMA_NAME, "Void"));
    assertThat(resultScope.getListeners().get(1)).usingRecursiveComparison().isEqualTo(
        new ListenerDetails("create", ROOT_OF_TOPIC_NAME, SCHEMA_NAME,
            "com.epam.digital.data.platform.model.core.kafka.EntityId"));
    assertThat(resultScope.getListeners().get(2)).usingRecursiveComparison().isEqualTo(
        new ListenerDetails("delete", ROOT_OF_TOPIC_NAME, SCHEMA_NAME, "Void"));
  }
}
