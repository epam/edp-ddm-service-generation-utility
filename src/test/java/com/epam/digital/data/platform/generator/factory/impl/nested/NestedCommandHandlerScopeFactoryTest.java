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
import com.epam.digital.data.platform.generator.model.template.NestedCommandHandlerField;
import com.epam.digital.data.platform.generator.scope.NestedCommandHandlerScope;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.getSettings;
import static com.epam.digital.data.platform.generator.utils.NestedStructureUtils.mockNestedDbCatalog;
import static com.epam.digital.data.platform.generator.utils.NestedStructureUtils.mockSingleChildChainNestedStructure;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NestedCommandHandlerScopeFactoryTest {

  private NestedCommandHandlerScopeFactory nestedCommandHandlerScopeFactory;

  @Mock
  private NestedStructureProvider nestedStructureProvider;

  private final Context context = new Context(getSettings(), mockNestedDbCatalog());

  @BeforeEach
  void beforeEach() {
    nestedCommandHandlerScopeFactory =
        new NestedCommandHandlerScopeFactory(nestedStructureProvider);
  }

  @Test
  void expectValidEntityScopesAreCreated() {
    when(nestedStructureProvider.findAll())
            .thenReturn(singletonList(mockSingleChildChainNestedStructure()));

    var actualScopes = nestedCommandHandlerScopeFactory.create(context);

    var expectedApplicationScope = createApplicationScope();
    var expectedOrderScope = createOrderScope();
    assertThat(actualScopes).hasSize(2);
    assertThat(actualScopes.get(0)).usingRecursiveComparison().isEqualTo(expectedApplicationScope);
    assertThat(actualScopes.get(1)).usingRecursiveComparison().isEqualTo(expectedOrderScope);
  }

  private NestedCommandHandlerScope createApplicationScope() {
    var applicationScope = new NestedCommandHandlerScope();
    applicationScope.setClassName("NestingFlowApplicationNestedUpsertCommandHandler");
    applicationScope.setSchemaName("NestingFlowApplicationNested");
    applicationScope.setSimpleFields(List.of("name", "id"));
    applicationScope.setRootEntityName("application");
    applicationScope.setRootHandler("applicationUpsertCommandHandler");

    var orderNestedCommandHandler = new NestedCommandHandlerField();
    orderNestedCommandHandler.setName("nestingFlowOrderNestedUpsertCommandHandler");
    orderNestedCommandHandler.setChildField("order");
    orderNestedCommandHandler.setInjectionField("orderId");
    applicationScope.setNestedHandlers(singletonList(orderNestedCommandHandler));
    return applicationScope;
  }

  private NestedCommandHandlerScope createOrderScope() {
    var orderScope = new NestedCommandHandlerScope();
    orderScope.setClassName("NestingFlowOrderNestedUpsertCommandHandler");
    orderScope.setSchemaName("NestingFlowOrderNested");
    orderScope.setSimpleFields(List.of("name", "itemId", "id"));
    orderScope.setRootEntityName("order");
    orderScope.setRootHandler("orderUpsertCommandHandler");

    var transactionNestedCommandHandler = new NestedCommandHandlerField();
    transactionNestedCommandHandler.setName("transactionUpsertCommandHandler");
    transactionNestedCommandHandler.setChildField("transaction");
    transactionNestedCommandHandler.setInjectionField("transactionId");
    orderScope.setNestedHandlers(singletonList(transactionNestedCommandHandler));
    return orderScope;
  }
}
