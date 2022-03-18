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

package com.epam.digital.data.platform.generator.factory.impl.nested;

import com.epam.digital.data.platform.generator.metadata.NestedStructureProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.scope.CommandListenerScope;
import com.epam.digital.data.platform.generator.utils.ContextTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.epam.digital.data.platform.generator.factory.impl.nested.NestedListenerScopeFactory.UPSERT_OUTPUT_TYPE;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.getSettings;
import static com.epam.digital.data.platform.generator.utils.NestedStructureUtils.mockNestedDbCatalog;
import static com.epam.digital.data.platform.generator.utils.NestedStructureUtils.mockSingleChildChainNestedStructure;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NestedListenerScopeFactoryTest {

  private NestedListenerScopeFactory nestedListenerScopeFactory;

  @Mock
  private NestedStructureProvider nestedStructureProvider;

  private final Context context = new Context(getSettings(), mockNestedDbCatalog(),
      ContextTestUtils.emptyAsyncData());

  @BeforeEach
  void beforeEach() {
    nestedListenerScopeFactory = new NestedListenerScopeFactory(nestedStructureProvider);
  }

  @Test
  void expectValidListenerScopeIsCreated() {
    when(nestedStructureProvider.findAll()).thenReturn(singletonList(mockSingleChildChainNestedStructure()));

    var actualScope = nestedListenerScopeFactory.create(context).get(0);

    var expectedScope = new CommandListenerScope();
    expectedScope.setClassName("NestingFlowApplicationNestedUpsertListener");
    expectedScope.setSchemaName("NestingFlowApplicationNested");
    expectedScope.setRootOfTopicName("nesting-flow-application-nested");
    expectedScope.setOperation("upsert");
    expectedScope.setOutputType(UPSERT_OUTPUT_TYPE);
    expectedScope.setCommandHandler("NestingFlowApplicationNestedUpsertCommandHandler");

    assertThat(actualScope)
            .usingRecursiveComparison()
            .isEqualTo(expectedScope);
  }
}
