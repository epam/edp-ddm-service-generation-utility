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

import com.epam.digital.data.platform.generator.constraints.impl.CompositeConstraintProvider;
import com.epam.digital.data.platform.generator.metadata.EnumProvider;
import com.epam.digital.data.platform.generator.metadata.NestedStructureProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.model.template.Field;
import com.epam.digital.data.platform.generator.scope.ModelScope;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.getSettings;
import static com.epam.digital.data.platform.generator.utils.NestedStructureUtils.mockNestedDbCatalog;
import static com.epam.digital.data.platform.generator.utils.NestedStructureUtils.mockSingleChildChainNestedStructure;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NestedEntityScopeFactoryTest {

  private NestedEntityScopeFactory nestedEntityScopeFactory;

  @Mock
  private EnumProvider enumProvider;
  @Mock
  private CompositeConstraintProvider constraintProviders;
  @Mock
  private NestedStructureProvider nestedStructureProvider;

  private final Context context = new Context(getSettings(), mockNestedDbCatalog());

  @BeforeEach
  void beforeEach() {
    nestedEntityScopeFactory =
        new NestedEntityScopeFactory(enumProvider, constraintProviders, nestedStructureProvider);
  }

  @Test
  void expectValidEntityScopesAreCreated() {
    when(nestedStructureProvider.findAll())
            .thenReturn(Collections.singletonList(mockSingleChildChainNestedStructure()));

    var actualScopes = nestedEntityScopeFactory.create(context);

    var expectedApplicationScope = createApplicationScope();
    var expectedOrderScope = createOrderScope();
    assertThat(actualScopes)
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyInAnyOrder(expectedApplicationScope, expectedOrderScope);
  }

  private ModelScope createApplicationScope() {
    var applicationScope = new ModelScope();
    applicationScope.setClassName("NestingFlowApplicationNested");
    var idField = new Field(UUID.class.getCanonicalName(), "id", emptyList());
    var nameField = new Field(String.class.getCanonicalName(), "name", emptyList());
    var nestedOrderField = new Field("NestingFlowOrderNested", "order", emptyList());
    applicationScope.setFields(Sets.newHashSet(idField, nameField, nestedOrderField));
    return applicationScope;
  }

  private ModelScope createOrderScope() {
    var orderScope = new ModelScope();
    orderScope.setClassName("NestingFlowOrderNested");
    var idField = new Field(UUID.class.getCanonicalName(), "id", emptyList());
    var nameField = new Field(String.class.getCanonicalName(), "name", emptyList());
    var itemField = new Field(UUID.class.getCanonicalName(), "itemId", emptyList());
    var transactionField = new Field("Transaction", "transaction", emptyList());
    orderScope.setFields(Set.of(idField, nameField, itemField, transactionField));
    return orderScope;
  }
}
