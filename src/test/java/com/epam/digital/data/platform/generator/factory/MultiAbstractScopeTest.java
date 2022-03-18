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

package com.epam.digital.data.platform.generator.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.newCatalog;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withSearchConditionView;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withTable;

import com.epam.digital.data.platform.generator.model.AsyncData;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.utils.ContextTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Table;

class MultiAbstractScopeTest {

  private static final String APPLICABLE_PREFIX = "test_";
  private static final String APPLICABLE_VIEW = APPLICABLE_PREFIX + "view";
  private static final String APPLICABLE_VIEW_NAME = APPLICABLE_VIEW + "_v";

  TestMultiAbstractScope instance;

  Context ctx;

  @BeforeEach
  void setup() {
    instance = new TestMultiAbstractScope();

    Catalog catalog = newCatalog(
        withTable("some_table"),
        withSearchConditionView(APPLICABLE_VIEW));
    AsyncData asyncData = ContextTestUtils.emptyAsyncData();
    ctx = new Context(null, catalog, asyncData);
  }

  @Test
  void shouldMapOnlyApplicable() {
    var scopes = instance.create(ctx);

    assertThat(scopes).containsOnly(APPLICABLE_VIEW_NAME);
  }

  @Test
  void shouldComposeEndpoint() {
    var endpoint = instance.getEndpoint(APPLICABLE_VIEW_NAME);

    assertThat(endpoint).isEqualTo("/test-view");
  }

  static class TestMultiAbstractScope extends MultiAbstractScope<String> {

    @Override
    protected String map(Table table, Context context) {
      return table.getName();
    }

    @Override
    protected boolean isApplicable(Table table, Context context) {
      return table.getName().startsWith(APPLICABLE_PREFIX);
    }

    @Override
    public String getPath() {
      return null;
    }
  }
}
