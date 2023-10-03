/*
 * Copyright 2023 EPAM Systems.
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

import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.permissionmap.PermissionMap;
import com.epam.digital.data.platform.generator.scope.ReadFileControllerScope;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Struct;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.FILE_COLUMN_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.PK_COLUMN_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.TABLE_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.emptyAsyncData;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.getSettings;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.newCatalog;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withColumn;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withTable;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withUuidPk;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReadFileControllerScopeFactoryTest {

  private static final Set<String> ROLE_SET = Set.of("hasRole('role1')", "hasRole('role2')");

  @Mock private PermissionMap permissionMap;

  private ReadFileControllerScopeFactory instance;

  private Context context;

  @BeforeEach
  void setup() {
    context =
        new Context(
            getSettings(),
            newCatalog(
                withTable(
                    TABLE_NAME,
                    withUuidPk(PK_COLUMN_NAME),
                    withColumn(FILE_COLUMN_NAME, Struct.class, "type_file"),
                    withColumn("scan_copy", Object.class, "_type_file"))),
            emptyAsyncData());
    instance = new ReadFileControllerScopeFactory(permissionMap);
  }

  @Test
  void expectValidScopeIsCreated() {
    when(permissionMap.getReadExpressionsForTable(TABLE_NAME, FILE_COLUMN_NAME)).thenReturn(ROLE_SET);
    when(permissionMap.getReadExpressionsForTable(TABLE_NAME, "scan_copy"))
        .thenReturn(Collections.emptySet());

    List<ReadFileControllerScope> actual = instance.create(context);

    assertThat(actual).hasSize(2);

    var fileColumnScope = actual.get(0);
    assertThat(fileColumnScope.getClassName()).isEqualTo("TestSchemaColumnFileReadController");
    assertThat(fileColumnScope.getServiceName()).isEqualTo("TestSchemaColumnFileReadService");
    assertThat(fileColumnScope.getEndpoint()).isEqualTo("/files/test-schema");
    assertThat(fileColumnScope.getColumnEndpoint()).isEqualTo("column-file");
    assertThat(fileColumnScope.getPkType()).isEqualTo(UUID.class.getCanonicalName());
    assertThat(fileColumnScope.getReadRoles()).containsExactlyElementsOf(ROLE_SET);

    var scanCopyScope = actual.get(1);
    assertThat(scanCopyScope.getClassName()).isEqualTo("TestSchemaScanCopyReadController");
    assertThat(scanCopyScope.getServiceName()).isEqualTo("TestSchemaScanCopyReadService");
    assertThat(scanCopyScope.getEndpoint()).isEqualTo("/files/test-schema");
    assertThat(scanCopyScope.getColumnEndpoint()).isEqualTo("scan-copy");
    assertThat(scanCopyScope.getPkType()).isEqualTo(UUID.class.getCanonicalName());
    assertThat(scanCopyScope.getReadRoles()).isEmpty();
  }
}
