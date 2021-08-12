package com.epam.digital.data.platform.generator.factory.impl;

import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.COLUMN_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.FILE_COLUMN_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.PK_COLUMN_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.SCHEMA_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.TABLE_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.getBlueprint;
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
                getBlueprint(),
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
            "com.epam.digital.data.platform.kafkaapi.core.util.JooqDataTypes.FILE_DATA_TYPE");
    assertThat(resultScope.getOutputFields().get(2).getName()).isEqualTo(PK_COLUMN_NAME);
    assertThat(resultScope.getOutputFields().get(2).getConverter()).isNull();
  }
}
