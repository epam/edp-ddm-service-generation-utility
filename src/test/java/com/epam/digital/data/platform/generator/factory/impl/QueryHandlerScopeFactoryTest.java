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
import static org.mockito.Mockito.when;

import com.epam.digital.data.platform.generator.metadata.NestedReadEntity;
import com.epam.digital.data.platform.generator.metadata.NestedReadProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.model.template.SelectableField;
import com.epam.digital.data.platform.generator.scope.QueryHandlerScope;
import com.epam.digital.data.platform.generator.utils.ContextTestUtils;
import java.sql.Struct;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class QueryHandlerScopeFactoryTest {

  private static final String MANY_TO_MANY_COLUMN = "m2m_column";
  private static final String ONE_TO_MANY_COLUMN = "o2m_column";

  private static final String RELATED_TABLE = "related_table";
  private static final String RELATED_TABLE_SCHEMA = "RelatedTable";
  private static final String RELATED_TABLE_COLUMN = "related_table_column";

  @Mock
  private NestedReadProvider nestedReadProvider;

  private QueryHandlerScopeFactory queryHandlerScopeFactory;

  @BeforeEach
  void setup() {
    queryHandlerScopeFactory = new QueryHandlerScopeFactory(nestedReadProvider);
  }

  @Test
  void queryHandlerScopeFactoryTest() {
    Context context =
        new Context(
            getSettings(),
            newCatalog(
                withTable(
                    TABLE_NAME,
                    withUuidPk(PK_COLUMN_NAME),
                    withTextColumn(COLUMN_NAME),
                    withColumn(MANY_TO_MANY_COLUMN, Object.class, "_uuid"),
                    withColumn(ONE_TO_MANY_COLUMN, UUID.class, "uuid"),
                    withColumn(FILE_COLUMN_NAME, Struct.class, "type_file")),
                withTable(RELATED_TABLE,
                        withUuidPk(PK_COLUMN_NAME),
                        withTextColumn(RELATED_TABLE_COLUMN))),
            ContextTestUtils.emptyAsyncData());
    when(nestedReadProvider.findFor(TABLE_NAME))
        .thenReturn(
            Map.of(
                MANY_TO_MANY_COLUMN,
                new NestedReadEntity(TABLE_NAME, MANY_TO_MANY_COLUMN, RELATED_TABLE),
                ONE_TO_MANY_COLUMN,
                new NestedReadEntity(TABLE_NAME, ONE_TO_MANY_COLUMN, RELATED_TABLE)));

    List<QueryHandlerScope> resultList =
        queryHandlerScopeFactory.create(context);

    assertThat(resultList).hasSize(2);
    QueryHandlerScope nestedResultScope = resultList.get(0);
    assertThat(nestedResultScope.getClassName()).isEqualTo(SCHEMA_NAME + "QueryHandler");
    assertThat(nestedResultScope.getSchemaName()).isEqualTo(SCHEMA_NAME + "ReadNested");
    assertThat(nestedResultScope.getTableName()).isEqualTo(TABLE_NAME);
    assertThat(nestedResultScope.getPkColumnName()).isEqualTo(PK_COLUMN_NAME);
    assertThat(nestedResultScope.getPkType()).isEqualTo(UUID.class.getCanonicalName());
    assertThat(nestedResultScope.getProviderName()).isEqualTo(SCHEMA_NAME + "TableDataProvider");

    assertThat(nestedResultScope.getSimpleSelectableFields()).hasSize(3);
    assertThat(nestedResultScope.getSimpleSelectableFields().get(0).getName()).isEqualTo(COLUMN_NAME);
    assertThat(nestedResultScope.getSimpleSelectableFields().get(0).getConverter()).isNull();
    assertThat(nestedResultScope.getSimpleSelectableFields().get(1).getName()).isEqualTo(FILE_COLUMN_NAME);
    assertThat(nestedResultScope.getSimpleSelectableFields().get(1).getConverter())
        .isEqualTo(
            "com.epam.digital.data.platform.restapi.core.utils.JooqDataTypes.FILE_DATA_TYPE");
    assertThat(nestedResultScope.getSimpleSelectableFields().get(2).getName()).isEqualTo(PK_COLUMN_NAME);
    assertThat(nestedResultScope.getSimpleSelectableFields().get(2).getConverter()).isNull();

    assertThat(nestedResultScope.getNestedSingleSelectableGroups()).containsOnlyKeys(ONE_TO_MANY_COLUMN);
    assertThat(nestedResultScope.getNestedSingleSelectableGroups().get(ONE_TO_MANY_COLUMN)
            .getTableName()).isEqualTo(RELATED_TABLE);
    assertThat(nestedResultScope.getNestedSingleSelectableGroups().get(ONE_TO_MANY_COLUMN)
            .getPkName()).isEqualTo(PK_COLUMN_NAME);
    assertThat(nestedResultScope.getNestedSingleSelectableGroups().get(ONE_TO_MANY_COLUMN).getFields())
        .usingRecursiveFieldByFieldElementComparator()
        .containsOnly(
            new SelectableField(PK_COLUMN_NAME, RELATED_TABLE, null),
            new SelectableField(RELATED_TABLE_COLUMN, RELATED_TABLE, null));

    assertThat(nestedResultScope.getNestedListSelectableGroups()).containsOnlyKeys(MANY_TO_MANY_COLUMN);
    assertThat(nestedResultScope.getNestedListSelectableGroups().get(MANY_TO_MANY_COLUMN)
            .getTableName()).isEqualTo(RELATED_TABLE);
    assertThat(nestedResultScope.getNestedListSelectableGroups().get(MANY_TO_MANY_COLUMN)
            .getPkName()).isEqualTo(PK_COLUMN_NAME);
    assertThat(nestedResultScope.getNestedListSelectableGroups().get(MANY_TO_MANY_COLUMN).getFields())
            .usingRecursiveFieldByFieldElementComparator()
            .containsOnly(
                    new SelectableField(PK_COLUMN_NAME, RELATED_TABLE, null),
                    new SelectableField(RELATED_TABLE_COLUMN, RELATED_TABLE, null));

    assertThat(nestedResultScope.getTableAccessCheckFields())
        .containsOnlyKeys(TABLE_NAME, RELATED_TABLE);
    assertThat(nestedResultScope.getTableAccessCheckFields().get(TABLE_NAME))
        .containsExactlyInAnyOrder(
            PK_COLUMN_NAME, COLUMN_NAME, FILE_COLUMN_NAME, ONE_TO_MANY_COLUMN, MANY_TO_MANY_COLUMN);
    assertThat(nestedResultScope.getTableAccessCheckFields().get(RELATED_TABLE))
        .containsExactlyInAnyOrder(PK_COLUMN_NAME, RELATED_TABLE_COLUMN);

    QueryHandlerScope simpleResultScope = resultList.get(1);
    assertThat(simpleResultScope.getClassName()).isEqualTo(RELATED_TABLE_SCHEMA + "QueryHandler");
    assertThat(simpleResultScope.getSchemaName()).isEqualTo(RELATED_TABLE_SCHEMA);
    assertThat(simpleResultScope.getTableName()).isEqualTo(RELATED_TABLE);
    assertThat(simpleResultScope.getPkColumnName()).isEqualTo(PK_COLUMN_NAME);
    assertThat(simpleResultScope.getPkType()).isEqualTo(UUID.class.getCanonicalName());
    assertThat(simpleResultScope.getProviderName()).isEqualTo(RELATED_TABLE_SCHEMA + "TableDataProvider");
    assertThat(simpleResultScope.getSimpleSelectableFields()).hasSize(2);
    assertThat(simpleResultScope.getSimpleSelectableFields())
            .usingRecursiveFieldByFieldElementComparator()
                    .containsOnly(new SelectableField(PK_COLUMN_NAME, RELATED_TABLE, null),
                            new SelectableField(RELATED_TABLE_COLUMN, RELATED_TABLE, null));
    assertThat(simpleResultScope.getTableAccessCheckFields())
            .containsOnlyKeys(RELATED_TABLE);
    assertThat(simpleResultScope.getTableAccessCheckFields().get(RELATED_TABLE))
            .containsExactlyInAnyOrder(
                    PK_COLUMN_NAME, RELATED_TABLE_COLUMN);
  }
}
