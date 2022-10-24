/*
 * Copyright 2022 EPAM Systems.
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

import com.epam.digital.data.platform.generator.metadata.BulkLoadInfoProvider;
import com.epam.digital.data.platform.generator.model.Context;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.SCHEMA_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.TABLE_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.getContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CreateCsvServiceScopeFactoryTest {

  @Mock
  private BulkLoadInfoProvider bulkLoadInfoProvider;

  private CreateCsvServiceScopeFactory instance;

  private final Context context = getContext();

  @BeforeEach
  void setup() {
    instance = new CreateCsvServiceScopeFactory(bulkLoadInfoProvider);
  }

  @Test
  void expectValidScopeIsCreated() {
    given(bulkLoadInfoProvider.getTablesWithBulkLoad()).willReturn(Set.of(TABLE_NAME));

    var resultList = instance.create(context);

    assertThat(resultList).hasSize(1);

    var resultScope = resultList.get(0);

    assertThat(resultScope.getClassName()).isEqualTo("TestSchemaCreateCsvService");
    assertThat(resultScope.getSchemaName()).isEqualTo(SCHEMA_NAME);
  }
}
