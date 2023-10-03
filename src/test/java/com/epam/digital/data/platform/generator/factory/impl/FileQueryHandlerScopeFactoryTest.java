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
import com.epam.digital.data.platform.generator.utils.ContextTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Struct;
import java.util.UUID;

import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.FILE_COLUMN_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.PK_COLUMN_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.TABLE_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.getSettings;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.newCatalog;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withColumn;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withTable;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withUuidPk;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class FileQueryHandlerScopeFactoryTest {

  private final FileQueryHandlerScopeFactory instance = new FileQueryHandlerScopeFactory();

  private Context context;

  @BeforeEach
  void beforeEach() {
    context =
            new Context(
                    getSettings(),
                    newCatalog(
                            withTable(
                                    TABLE_NAME,
                                    withUuidPk(PK_COLUMN_NAME),
                                    withColumn(FILE_COLUMN_NAME, Struct.class, "type_file"),
                                    withColumn("scan_copy", Object.class, "_type_file"))),
                    ContextTestUtils.emptyAsyncData());
  }

  @Test
  void expectValidScopeCreated() {
    var actual = instance.create(context);

    assertThat(actual).hasSize(2);
    var columnFileScope = actual.get(0);
    assertThat(columnFileScope.getClassName()).isEqualTo("TestSchemaColumnFileQueryHandler");
    assertThat(columnFileScope.getSchemaName()).isEqualTo("TestSchemaRead");
    assertThat(columnFileScope.getPkType()).isEqualTo(UUID.class.getCanonicalName());
    assertThat(columnFileScope.getTableDataProviderName()).isEqualTo("TestSchemaTableDataProvider");
    assertThat(columnFileScope.getSimpleSelectableFields()).hasSize(1);
    assertThat(columnFileScope.getSimpleSelectableFields().get(0).getName())
        .isEqualTo(FILE_COLUMN_NAME);
    assertThat(columnFileScope.getSimpleSelectableFields().get(0).getConverter())
        .isEqualTo(
            "com.epam.digital.data.platform.restapi.core.utils.JooqDataTypes.FILE_DATA_TYPE");

    var scanCopyScope = actual.get(1);
    assertThat(scanCopyScope.getClassName()).isEqualTo("TestSchemaScanCopyQueryHandler");
    assertThat(scanCopyScope.getSchemaName()).isEqualTo("TestSchemaRead");
    assertThat(scanCopyScope.getPkType()).isEqualTo(UUID.class.getCanonicalName());
    assertThat(scanCopyScope.getTableDataProviderName()).isEqualTo("TestSchemaTableDataProvider");
    assertThat(scanCopyScope.getSimpleSelectableFields()).hasSize(1);
    assertThat(scanCopyScope.getSimpleSelectableFields().get(0).getName())
            .isEqualTo("scan_copy");
    assertThat(scanCopyScope.getSimpleSelectableFields().get(0).getConverter())
            .isEqualTo(
                    "com.epam.digital.data.platform.restapi.core.utils.JooqDataTypes.FILE_ARRAY_DATA_TYPE");
  }
}
