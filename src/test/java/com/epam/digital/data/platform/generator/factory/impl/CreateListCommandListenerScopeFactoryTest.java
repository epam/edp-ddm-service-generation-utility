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
import com.epam.digital.data.platform.generator.metadata.PartialUpdate;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.scope.ListenerScope;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.SCHEMA_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.TABLE_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.getContext;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withTable;
import static com.epam.digital.data.platform.generator.utils.TestUtils.findByStringContains;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateListCommandListenerScopeFactoryTest {

  @Mock
  private BulkLoadInfoProvider bulkLoadInfoProvider;

  private CreateListCommandListenerScopeFactory instance;

  @BeforeEach
  void setup() {
    instance = new CreateListCommandListenerScopeFactory(bulkLoadInfoProvider);
  }

  @Test
  void expectValidScopeIsCreated() {
    when(bulkLoadInfoProvider.getTablesWithBulkLoad()).thenReturn(Set.of(TABLE_NAME));

    var actual = instance.create(getContext());

    assertThat(actual).hasSize(1);

    var expected = new ListenerScope();
    expected.setClassName("TestSchemaCreateListListener");
    expected.setSchemaName("TestSchemaCreateList");
    expected.setOperation("create-list");
    expected.setRootOfTopicName("test-schema");
    expected.setHandlerName("TestSchemaCreateListCommandHandler");
    expected.setOutputType(
        "java.util.List<com.epam.digital.data.platform.model.core.kafka.EntityId>");
    assertThat(actual.get(0)).usingRecursiveComparison().isEqualTo(expected);
  }
}
