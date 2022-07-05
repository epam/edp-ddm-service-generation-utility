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

import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.SEARCH_SCHEMA_NAME;
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

class SearchListenerScopeFactoryTest {

  private static final String ROOT_OF_TOPIC_NAME = "test-schema-search";

  private SearchListenerScopeFactory listenerScopeFactory;

  @BeforeEach
  void setup() {
    listenerScopeFactory = new SearchListenerScopeFactory();
  }

  @Test
  void listenerScopeFactoryTest() {
    Set<String> searchConditions = new HashSet<>(List.of(ContextTestUtils.VIEW_NAME.concat("_v")));
    Context context = new Context(ContextTestUtils.getSettings(),
        ContextTestUtils.getCatalog(), new AsyncData(new HashSet<>(), searchConditions));
    List<ListenerScope> resultList = listenerScopeFactory.create(context);

    assertThat(resultList).hasSize(1);

    ListenerScope resultScope = resultList.get(0);
    assertThat(resultScope.getClassName()).isEqualTo(SEARCH_SCHEMA_NAME + "Listener");
    assertThat(resultScope.getSchemaName()).isEqualTo(SEARCH_SCHEMA_NAME);

    assertThat(resultScope.getOperation()).isEqualTo("search");
    assertThat(resultScope.getRootOfTopicName()).isEqualTo(ROOT_OF_TOPIC_NAME);
    assertThat(resultScope.getOutputType()).isEqualTo(SEARCH_SCHEMA_NAME);
    assertThat(resultScope.getHandlerName()).isEqualTo(SEARCH_SCHEMA_NAME + "SearchHandler");
  }

  @Test
  void emptyListenerScopeWithSyncContextTest() {
    List<ListenerScope> resultList = listenerScopeFactory.create(getContext());

    assertThat(resultList).hasSize(0);
  }
}