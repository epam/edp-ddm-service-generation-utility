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

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.assertj.core.api.Assertions.assertThat;

import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.scope.ServiceScope;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class AbstractScopeTest {

  static class TestScope extends AbstractScope {

    @Override
    public List<ServiceScope> create(Context context) {
      return null;
    }

    @Override
    public String getPath() {
      return EMPTY;
    }
  }

  TestScope instance = new TestScope();

  @ParameterizedTest
  @ValueSource(strings = {"some_table", "some_table_hst", "some_table_rel_v", "some_table_v"})
  void shouldCutOutSuffixInTableNames(String rawTableName) {
    assertThat(instance.getCutTableName(rawTableName)).isEqualTo("some_table");
  }

  @Disabled("does not work when the input data is already a SchemaName")
  @ParameterizedTest
  @ValueSource(strings = {"some_table", "some-table", "someTable", "SomeTable"})
  void shouldExtractSchemaNames(String input) {
    assertThat(instance.getSchemaName(input)).isEqualTo("SomeTable");
  }
}
