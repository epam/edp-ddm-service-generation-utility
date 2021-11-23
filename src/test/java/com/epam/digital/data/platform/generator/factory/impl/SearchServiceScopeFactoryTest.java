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
