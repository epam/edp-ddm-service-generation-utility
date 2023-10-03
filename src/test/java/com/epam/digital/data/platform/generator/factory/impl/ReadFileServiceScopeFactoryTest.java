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
import com.epam.digital.data.platform.generator.scope.ReadFileServiceScope;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Struct;
import java.util.List;
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

@ExtendWith(MockitoExtension.class)
class ReadFileServiceScopeFactoryTest {

  private ReadFileServiceScopeFactory instance;

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
    instance = new ReadFileServiceScopeFactory();
  }

  @Test
  void expectValidScopeIsCreated() {

    List<ReadFileServiceScope> actual = instance.create(context);

    assertThat(actual).hasSize(2);

    var fileColumnScope = actual.get(0);
    assertThat(fileColumnScope.getClassName()).isEqualTo("TestSchemaColumnFileReadService");
    assertThat(fileColumnScope.getFileFieldName()).isEqualTo("columnFile");
    assertThat(fileColumnScope.isFieldTypeList()).isFalse();
    assertThat(fileColumnScope.getPkType()).isEqualTo(UUID.class.getCanonicalName());
    assertThat(fileColumnScope.getSchemaName()).isEqualTo("TestSchemaRead");
    assertThat(fileColumnScope.getHandlerName()).isEqualTo("TestSchemaColumnFileQueryHandler");

    var scanCopyScope = actual.get(1);
    assertThat(scanCopyScope.getClassName()).isEqualTo("TestSchemaScanCopyReadService");
    assertThat(scanCopyScope.getFileFieldName()).isEqualTo("scanCopy");
    assertThat(scanCopyScope.isFieldTypeList()).isTrue();
    assertThat(scanCopyScope.getPkType()).isEqualTo(UUID.class.getCanonicalName());
    assertThat(scanCopyScope.getSchemaName()).isEqualTo("TestSchemaRead");
    assertThat(scanCopyScope.getHandlerName()).isEqualTo("TestSchemaScanCopyQueryHandler");
  }
}
