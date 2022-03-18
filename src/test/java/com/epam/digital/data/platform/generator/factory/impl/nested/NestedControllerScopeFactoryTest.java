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
import com.epam.digital.data.platform.generator.permissionmap.PermissionMap;
import com.epam.digital.data.platform.generator.scope.ControllerScope;
import com.epam.digital.data.platform.generator.utils.ContextTestUtils;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Set;

import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.getSettings;
import static com.epam.digital.data.platform.generator.utils.NestedStructureUtils.mockNestedDbCatalog;
import static com.epam.digital.data.platform.generator.utils.NestedStructureUtils.mockSingleChildChainNestedStructure;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NestedControllerScopeFactoryTest {

  private static final Set<String> RBAC_EXPRESSION = Set.of("expression1, expression2");

  private NestedControllerScopeFactory nestedControllerScopeFactory;

  @Mock
  private NestedStructureProvider nestedStructureProvider;
  @Mock
  private PermissionMap permissionMap;

  private final Context context = new Context(getSettings(), mockNestedDbCatalog(),
      ContextTestUtils.emptyAsyncData());

  @BeforeEach
  void beforeEach() {
    nestedControllerScopeFactory =
        new NestedControllerScopeFactory(nestedStructureProvider, permissionMap);
  }

  @Test
  void expectValidControllerScopeCreated() {
    when(nestedStructureProvider.findAll())
        .thenReturn(Collections.singletonList(mockSingleChildChainNestedStructure()));
    when(permissionMap.getCreateExpressionsFor(Set.of("application", "order", "transaction")))
        .thenReturn(RBAC_EXPRESSION);

    var actualScope = nestedControllerScopeFactory.create(context).get(0);

    var expectedScope = new ControllerScope();
    expectedScope.setClassName("NestingFlowApplicationNestedController");
    expectedScope.setEndpoint("/nesting-flow");
    expectedScope.setSchemaName("NestingFlowApplicationNested");
    expectedScope.setCreateRoles(Lists.newArrayList(RBAC_EXPRESSION));

    assertThat(actualScope).usingRecursiveComparison().isEqualTo(expectedScope);
  }
}