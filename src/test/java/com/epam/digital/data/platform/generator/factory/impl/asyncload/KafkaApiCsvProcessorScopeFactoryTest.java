/*
 * Copyright 2023 EPAM Systems.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.epam.digital.data.platform.generator.factory.impl.asyncload;

import com.epam.digital.data.platform.generator.factory.impl.asyncload.AsyncDataLoadCsvProcessorKafkaScopeFactory;
import com.epam.digital.data.platform.generator.metadata.AsyncDataLoadInfoProvider;
import com.epam.digital.data.platform.generator.metadata.NestedStructureProvider;
import com.epam.digital.data.platform.generator.model.Context;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.TABLE_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.getContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaApiCsvProcessorScopeFactoryTest {


  private static final String LIMIT = "100";
  private AsyncDataLoadCsvProcessorKafkaScopeFactory instance;

  @Mock
  private AsyncDataLoadInfoProvider asyncDataLoadInfoProvider;
  @Mock
  private NestedStructureProvider nestedStructureProvider;

  private final Context context = getContext();

  @BeforeEach
  void beforeEach() {
    instance = new AsyncDataLoadCsvProcessorKafkaScopeFactory(asyncDataLoadInfoProvider,
        nestedStructureProvider);
  }

  @Test
  void expectValidScopeIsCreated() {
    when(asyncDataLoadInfoProvider.getTablesWithAsyncLoad()).thenReturn(Map.of(TABLE_NAME, LIMIT));

    var expectedScopes = instance.create(context);

    assertThat(expectedScopes).hasSize(1);
    assertThat(expectedScopes.get(0).getClassName()).isEqualTo("TestSchemaModelAsyncDataLoadCsvProcessor");
    assertThat(expectedScopes.get(0).getCsvRowSchemaName()).isEqualTo("TestSchemaModel");
    assertThat(expectedScopes.get(0).getCsvPayloadSchemaName()).isEqualTo("TestSchemaModelArrayCsv");
  }
}