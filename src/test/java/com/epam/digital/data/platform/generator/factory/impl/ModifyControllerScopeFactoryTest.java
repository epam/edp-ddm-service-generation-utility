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

package com.epam.digital.data.platform.generator.factory.impl;

import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.ENDPOINT;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.PK_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.SCHEMA_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.TABLE_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.VIEW_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.getContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.epam.digital.data.platform.generator.metadata.BulkLoadInfoProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.permissionmap.PermissionMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.epam.digital.data.platform.generator.scope.ModifyControllerScope;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ModifyControllerScopeFactoryTest {

  @Mock
  private PermissionMap permissionMap;
  @Mock
  private BulkLoadInfoProvider bulkLoadInfoProvider;

  private ModifyControllerScopeFactory instance;

  private static final Context context = getContext();

  private static final Set<String> DENY_ALL_LIST = Set.of("denyAll");
  private static final Set<String> ROLE_LIST = Set.of("hasRole('role1')", "hasRole('role2')");

  @BeforeEach
  void setup() {
    instance = new ModifyControllerScopeFactory(permissionMap, bulkLoadInfoProvider);
  }

  @Test
  void shouldCreateControllerScope() {
    List<ModifyControllerScope> resultList = instance.create(context);

    assertThat(resultList).hasSize(1);

    ModifyControllerScope resultScope = resultList.get(0);
    assertThat(resultScope.getClassName()).isEqualTo(SCHEMA_NAME + "ModifyController");
    assertThat(resultScope.getSchemaName()).isEqualTo(SCHEMA_NAME);
    assertThat(resultScope.getPkName()).isEqualTo(PK_NAME);
    assertThat(resultScope.getEndpoint()).isEqualTo(ENDPOINT);
    assertThat(resultScope.getPkType()).isEqualTo(UUID.class.getCanonicalName());
    assertThat(resultScope.isBulkLoadEnabled()).isFalse();
  }

  @Test
  void shouldAddRolesToControllerScope() {
    given(permissionMap.getUpdateExpressionsFor(TABLE_NAME)).willReturn(DENY_ALL_LIST);
    given(permissionMap.getCreateExpressionsFor(TABLE_NAME)).willReturn(ROLE_LIST);
    given(permissionMap.getDeleteExpressionsFor(TABLE_NAME)).willReturn(ROLE_LIST);

    List<ModifyControllerScope> resultList = instance.create(context);

    ModifyControllerScope resultScope = resultList.get(0);
    assertThat(resultScope.getUpdateRoles()).containsAll(DENY_ALL_LIST);
    assertThat(resultScope.getCreateRoles()).containsAll(ROLE_LIST);
    assertThat(resultScope.getDeleteRoles()).containsAll(ROLE_LIST);
  }

  @Test
  void shouldEnableBulkLoadForControllerScope() {
    given(bulkLoadInfoProvider.getTablesWithBulkLoad()).willReturn(Set.of(TABLE_NAME, VIEW_NAME));

    List<ModifyControllerScope> resultList = instance.create(context);

    ModifyControllerScope resultScope = resultList.get(0);
    assertThat(resultScope.isBulkLoadEnabled()).isTrue();
  }
}