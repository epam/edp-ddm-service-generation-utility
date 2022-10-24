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
import com.epam.digital.data.platform.generator.permissionmap.PermissionMap;
import com.epam.digital.data.platform.generator.scope.ModifyControllerScope;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.ENDPOINT;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.SCHEMA_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.TABLE_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.getContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BulkLoadControllerScopeFactoryTest {

  @Mock
  private PermissionMap permissionMap;
  @Mock
  private BulkLoadInfoProvider bulkLoadInfoProvider;

  private BulkLoadControllerScopeFactory instance;

  private static final Context context = getContext();

  private static final Set<String> ROLE_LIST = Set.of("hasRole('role1')", "hasRole('role2')");

  @BeforeEach
  void setup() {
    instance = new BulkLoadControllerScopeFactory(permissionMap, bulkLoadInfoProvider);
  }

  @Test
  void shouldCreateControllerScope() {
    when(bulkLoadInfoProvider.getTablesWithBulkLoad()).thenReturn(Set.of(TABLE_NAME));
    when(permissionMap.getCreateExpressionsFor(TABLE_NAME)).thenReturn(ROLE_LIST);

    List<ModifyControllerScope> resultList = instance.create(context);

    assertThat(resultList).hasSize(1);

    ModifyControllerScope resultScope = resultList.get(0);
    assertThat(resultScope.getClassName()).isEqualTo(SCHEMA_NAME + "BulkLoadController");
    assertThat(resultScope.getSchemaName()).isEqualTo(SCHEMA_NAME);
    assertThat(resultScope.getEndpoint()).isEqualTo(ENDPOINT);
    assertThat(resultScope.getCreateRoles()).containsExactlyElementsOf(ROLE_LIST);
  }
}