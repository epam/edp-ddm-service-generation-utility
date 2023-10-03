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

package com.epam.digital.data.platform.generator.permissionmap;

import static org.assertj.core.api.Assertions.assertThat;

import com.epam.digital.data.platform.generator.config.DataSourceEnable;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@DataSourceEnable
@SpringBootTest(classes = PermissionMap.class)
class PermissionMapIT {

  @Autowired
  PermissionMap instance;

  @Test
  void shouldFindReads() {
    var roles = instance.getCreateExpressionsFor("pd_processing_consent");

    assertThat(roles).containsExactlyInAnyOrder("denyAll");
  }

  @Test
  void shouldFindReadRolesOnlyForTable() {
    var roles = instance.getReadExpressionsForTable("pb_table_read_role");

    assertThat(roles).containsExactlyInAnyOrder(
        String.format(PermissionMap.HAS_ANY_ROLE, "officer"));
  }

  @Test
  void shouldFindReadRolesByTableType() {
    var roles = instance.getReadExpressionsForByType("pb_type_read", PermissionObjectType.TABLE);

    assertThat(roles).containsExactlyInAnyOrder(
        String.format(PermissionMap.HAS_ANY_ROLE, "officer"));
  }

  @Test
  void shouldFindReadRolesBySearchConditionType() {
    var roles = instance.getReadExpressionsForByType("pb_type_read",
        PermissionObjectType.SEARCH_CONDITION);

    assertThat(roles).containsExactlyInAnyOrder(
        String.format(PermissionMap.HAS_ANY_ROLE, "op-regression"));
  }

  @Test
  void shouldFindTablesReadRoles() {
    var roles = instance.getReadExpressionsForTables(
        Set.of("pb_tables_read_roles_1",
            "pb_tables_read_roles_2"));

    assertThat(roles).containsExactlyInAnyOrder(
        String.format(PermissionMap.HAS_ANY_ROLE, "officer"),
        String.format(PermissionMap.HAS_ANY_ROLE, "op-regression"));
  }

  @Test
  void shouldReturnColumnAndTableReadRoles() {
    var roles = instance.getReadExpressionsForTable("pb_column_read_roles", "first_column");

    assertThat(roles).containsExactlyInAnyOrder(
        String.format(PermissionMap.HAS_ANY_ROLE, "officer','op-regression"));
  }
}
