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

import com.epam.digital.data.platform.generator.factory.impl.asyncload.AsyncDataLoadControllerScopeFactory;
import com.epam.digital.data.platform.generator.metadata.AsyncDataLoadInfoProvider;
import com.epam.digital.data.platform.generator.metadata.NestedStructureProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.permissionmap.PermissionMap;
import com.epam.digital.data.platform.generator.scope.ModifyControllerScope;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsyncDataLoadControllerScopeFactoryTest {

  private static final String LIMIT = "100";
  @Mock
  private PermissionMap permissionMap;
  @Mock
  private AsyncDataLoadInfoProvider asyncDataLoadInfoProvider;
  @Mock
  private NestedStructureProvider nestedStructureProvider;

  private AsyncDataLoadControllerScopeFactory instance;

  private static final Context context = getContext();

  private static final Set<String> ROLE_LIST = Set.of("hasRole('role1')", "hasRole('role2')");

  @BeforeEach
  void setup() {
    instance = new AsyncDataLoadControllerScopeFactory(permissionMap, asyncDataLoadInfoProvider,
        nestedStructureProvider);
  }

  @Test
  void shouldCreateControllerScope() {
    when(asyncDataLoadInfoProvider.getTablesWithAsyncLoad()).thenReturn(Map.of(TABLE_NAME, LIMIT));
    when(permissionMap.getCreateExpressionsFor(TABLE_NAME)).thenReturn(ROLE_LIST);

    List<ModifyControllerScope> resultList = instance.create(context);

    assertThat(resultList).hasSize(1);

    ModifyControllerScope resultScope = resultList.get(0);
    assertThat(resultScope.getClassName()).isEqualTo(SCHEMA_NAME + "AsyncDataLoadController");
    assertThat(resultScope.getSchemaName()).isEqualTo(SCHEMA_NAME);
    assertThat(resultScope.getEndpoint()).isEqualTo("/v2" + ENDPOINT);
    assertThat(resultScope.getCreateRoles()).containsExactlyElementsOf(ROLE_LIST);
  }
}