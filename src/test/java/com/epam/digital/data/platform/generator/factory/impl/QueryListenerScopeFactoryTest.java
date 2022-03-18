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

import com.epam.digital.data.platform.generator.model.AsyncData;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.scope.ListenerScope;
import com.epam.digital.data.platform.generator.utils.ContextTestUtils;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class QueryListenerScopeFactoryTest {

  private static final String ROOT_OF_TOPIC_NAME = "test-schema";

  private QueryListenerScopeFactory listenerScopeFactory;

  @BeforeEach
  void setup() {
    listenerScopeFactory = new QueryListenerScopeFactory();
  }

  @Test
  void listenerScopeFactoryTest() {
    Set<String> tables = new HashSet<>(List.of(ContextTestUtils.TABLE_NAME));
    Context context = new Context(ContextTestUtils.getSettings(),
        ContextTestUtils.getCatalog(), new AsyncData(tables, new HashSet<>()));
    List<ListenerScope> resultList = listenerScopeFactory.create(context);

    assertThat(resultList).hasSize(1);

    ListenerScope resultScope = resultList.get(0);
    assertThat(resultScope.getClassName()).isEqualTo(SCHEMA_NAME + "QueryListener");
    assertThat(resultScope.getSchemaName()).isEqualTo(SCHEMA_NAME);

    assertThat(resultScope.getOperation()).isEqualTo("read");
    assertThat(resultScope.getRootOfTopicName()).isEqualTo(ROOT_OF_TOPIC_NAME);
    assertThat(resultScope.getPkType()).isEqualTo("java.util.UUID");
    assertThat(resultScope.getOutputType()).isEqualTo(SCHEMA_NAME);
  }

  @Test
  void emptyListenerScopeWithSyncContextTest() {
    List<ListenerScope> resultList = listenerScopeFactory.create(getContext());

    assertThat(resultList).hasSize(0);
  }
}