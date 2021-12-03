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

import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.COLUMN_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.FILE_COLUMN_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.PK_COLUMN_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.SCHEMA_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.TABLE_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.getSettings;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.newCatalog;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withColumn;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withTable;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withTextColumn;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withUuidPk;
import static org.assertj.core.api.Assertions.assertThat;

import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.scope.QueryHandlerScope;
import java.sql.Struct;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class QueryHandlerScopeFactoryTest {

  private QueryHandlerScopeFactory queryHandlerScopeFactory;

  @BeforeEach
  void setup() {
    queryHandlerScopeFactory = new QueryHandlerScopeFactory();
  }

  @Test
  void queryHandlerScopeFactoryTest() {
    List<QueryHandlerScope> resultList =
        queryHandlerScopeFactory.create(
            new Context(
                getSettings(),
                newCatalog(
                    withTable(
                        TABLE_NAME,
                        withUuidPk(PK_COLUMN_NAME),
                        withTextColumn(COLUMN_NAME),
                        withColumn(FILE_COLUMN_NAME, Struct.class, "type_file")))));

    assertThat(resultList).hasSize(1);
    QueryHandlerScope resultScope = resultList.get(0);
    assertThat(resultScope.getClassName()).isEqualTo(SCHEMA_NAME + "QueryHandler");
    assertThat(resultScope.getSchemaName()).isEqualTo(SCHEMA_NAME);
    assertThat(resultScope.getTableName()).isEqualTo(TABLE_NAME);
    assertThat(resultScope.getPkColumnName()).isEqualTo(PK_COLUMN_NAME);
    assertThat(resultScope.getPkType()).isEqualTo(UUID.class.getCanonicalName());

    assertThat(resultScope.getOutputFields()).hasSize(3);
    assertThat(resultScope.getOutputFields().get(0).getName()).isEqualTo(COLUMN_NAME);
    assertThat(resultScope.getOutputFields().get(0).getConverter()).isNull();
    assertThat(resultScope.getOutputFields().get(1).getName()).isEqualTo(FILE_COLUMN_NAME);
    assertThat(resultScope.getOutputFields().get(1).getConverter())
        .isEqualTo(
            "com.epam.digital.data.platform.restapi.core.utils.JooqDataTypes.FILE_DATA_TYPE");
    assertThat(resultScope.getOutputFields().get(2).getName()).isEqualTo(PK_COLUMN_NAME);
    assertThat(resultScope.getOutputFields().get(2).getConverter()).isNull();
  }
}
