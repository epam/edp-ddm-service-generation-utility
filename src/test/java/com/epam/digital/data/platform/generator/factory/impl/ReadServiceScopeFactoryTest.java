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

import com.epam.digital.data.platform.generator.metadata.NestedReadEntity;
import com.epam.digital.data.platform.generator.metadata.NestedReadProvider;
import com.epam.digital.data.platform.generator.model.Context;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.UUID;

import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.COLUMN_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.PK_COLUMN_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.PK_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.SCHEMA_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.TABLE_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.emptyAsyncData;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.getSettings;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.newCatalog;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withColumn;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withTable;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withTextColumn;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withUuidPk;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReadServiceScopeFactoryTest {

  private static final String RELATED_TABLE_NAME = "related_table";

  @Mock
  private NestedReadProvider nestedReadProvider;
  private Context context;

  private ReadServiceScopeFactory instance;

  @BeforeEach
  void beforeEach() {
    context =
            new Context(
                    getSettings(),
                    newCatalog(
                            withTable(TABLE_NAME, withUuidPk(PK_COLUMN_NAME),
                                    withColumn(COLUMN_NAME, UUID.class, "uuid")),
                            withTable(RELATED_TABLE_NAME, withUuidPk(PK_COLUMN_NAME),
                                    withTextColumn("related_column"))),
                    emptyAsyncData());

    instance = new ReadServiceScopeFactory(nestedReadProvider);
  }

  @Test
  void expectSimpleReadServiceScopeIsCreated() {
    var actual = instance.create(context);

    assertThat(actual).hasSize(2);
    assertThat(actual.get(0).getClassName()).isEqualTo(SCHEMA_NAME + "ReadService");
    assertThat(actual.get(0).getSchemaName()).isEqualTo(SCHEMA_NAME);
    assertThat(actual.get(0).getPkName()).isEqualTo(PK_NAME);
    assertThat(actual.get(0).getPkType()).isEqualTo(UUID.class.getCanonicalName());
    assertThat(actual.get(0).getHandlerName()).isEqualTo(SCHEMA_NAME + "QueryHandler");
  }

  @Test
  void expectReadServiceScopeForNestedReadIsCreated() {
    when(nestedReadProvider.findFor(TABLE_NAME))
            .thenReturn(Map.of(COLUMN_NAME, new NestedReadEntity()));

    var actual = instance.create(context);

    assertThat(actual).hasSize(2);
    assertThat(actual.get(0).getClassName()).isEqualTo(SCHEMA_NAME + "ReadService");
    assertThat(actual.get(0).getSchemaName()).isEqualTo(SCHEMA_NAME + "ReadNested");
    assertThat(actual.get(0).getPkName()).isEqualTo(PK_NAME);
    assertThat(actual.get(0).getPkType()).isEqualTo(UUID.class.getCanonicalName());
    assertThat(actual.get(0).getHandlerName()).isEqualTo(SCHEMA_NAME + "QueryHandler");
  }
}
