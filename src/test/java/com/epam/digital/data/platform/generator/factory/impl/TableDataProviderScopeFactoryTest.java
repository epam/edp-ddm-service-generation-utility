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

import com.epam.digital.data.platform.generator.factory.TableDataProviderScopeFactory;
import org.junit.jupiter.api.Test;

import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.PK_COLUMN_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.SCHEMA_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.TABLE_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.getContext;
import static org.assertj.core.api.Assertions.assertThat;

class TableDataProviderScopeFactoryTest {

  private final TableDataProviderScopeFactory scopeFactory =
      new RestApiTableDataProviderScopeFactory();

  @Test
  void tableDataProviderScopeFactoryTest() {
    var actualScopes = scopeFactory.create(getContext());

    assertThat(actualScopes).hasSize(1);

    var resultScope = actualScopes.get(0);
    assertThat(resultScope.getClassName()).isEqualTo(SCHEMA_NAME + "TableDataProvider");
    assertThat(resultScope.getTableName()).isEqualTo(TABLE_NAME);
    assertThat(resultScope.getPkColumnName()).isEqualTo(PK_COLUMN_NAME);
  }
}
