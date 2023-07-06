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

import com.epam.digital.data.platform.generator.metadata.AsyncDataLoadInfoProvider;
import com.epam.digital.data.platform.generator.metadata.NestedStructureProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.StringUtils;

import java.util.Map;

import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Disabled
class AsyncDataLoadListenerScopeFactoryTest {

  private static final String LIMIT = "100";
  @Mock
  private AsyncDataLoadInfoProvider asyncDataLoadInfoProvider;
  @Mock
  private NestedStructureProvider nestedStructureProvider;

  private AsyncDataLoadListenerScopeFactory instance;

  @BeforeEach
  void beforeEach() {
    instance = new AsyncDataLoadListenerScopeFactory(asyncDataLoadInfoProvider,
        nestedStructureProvider);
  }

  @Test
  void expectValidScopeIsCreated() {
    when(asyncDataLoadInfoProvider.getTablesWithAsyncLoad()).thenReturn(Map.of(TABLE_NAME, LIMIT));

    var actual = instance.create(getContext());

    assertThat(actual).hasSize(1);

    var resultScope = actual.get(0);
    assertThat(resultScope.getClassName()).isEqualTo(SCHEMA_NAME + "UpsertAsyncDataLoadListener");
    assertThat(resultScope.getSchemaName()).isEqualTo(SCHEMA_NAME + "Model");

    assertThat(resultScope.getOperation()).isEqualTo("upsert");
    assertThat(resultScope.getHandlerName()).isEqualTo(SCHEMA_NAME + "UpsertCommandHandler");
    assertThat(resultScope.getFilterName()).isEqualTo(
        StringUtils.uncapitalize(SCHEMA_NAME) + "FilterStrategy");
    assertThat(resultScope.getCsvProcessorName()).isEqualTo(SCHEMA_NAME + "AsyncDataLoadCsvProcessor");
  }
}
