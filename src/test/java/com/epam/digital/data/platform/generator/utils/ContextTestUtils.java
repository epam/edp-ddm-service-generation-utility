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

package com.epam.digital.data.platform.generator.utils;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

import com.epam.digital.data.platform.generator.model.AsyncData;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.model.General;
import com.epam.digital.data.platform.generator.model.Kafka;
import com.epam.digital.data.platform.generator.model.RetentionPolicyInDays;
import com.epam.digital.data.platform.generator.model.Settings;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.PrimaryKey;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableConstraintColumn;
import schemacrawler.schema.TableType;

public class ContextTestUtils {

  public static final String TABLE_NAME = "test_schema";
  public static final String VIEW_NAME = "test_schema_search";
  public static final String SCHEMA_NAME = "TestSchema";
  public static final String SEARCH_SCHEMA_NAME = "TestSchemaSearch";
  public static final String PK_COLUMN_NAME = "pk_name";
  public static final String PK_NAME = "pkName";
  public static final String COLUMN_NAME = "column";
  public static final String FILE_COLUMN_NAME = "column_file";
  public static final String GEOMETRY_COLUMN_NAME = "column_geometry";
  public static final String ENDPOINT = "/test-schema";
  public static final String VERSION = "1.2.3";
  public static final String BASE_PACKAGE_NAME = "my.fav.registry";
  public static final String MAJOR_VERSION = "1.2";
  public static final Integer RETENTION_READ = 2;
  public static final Integer RETENTION_WRITE = 365;

  public static Settings getSettings() {
    var general = new General();
    general.setVersion(VERSION);
    general.setBasePackageName(BASE_PACKAGE_NAME);

    var retentionPolicyDays = new RetentionPolicyInDays();
    retentionPolicyDays.setRead(RETENTION_READ);
    retentionPolicyDays.setWrite(RETENTION_WRITE);

    var kafka = new Kafka();
    kafka.setRetentionPolicyInDays(retentionPolicyDays);

    var settings = new Settings();
    settings.setGeneral(general);
    settings.setKafka(kafka);

    return settings;
  }

  public static Context getContext() {
    return new Context(getSettings(), getCatalog(), emptyAsyncData());
  }

  public static Catalog getCatalog() {
    return newCatalog(
        withTable(TABLE_NAME, withUuidPk(PK_COLUMN_NAME)),
        withSearchConditionView(VIEW_NAME));
  }

  public static PrimaryKey withUuidPk(String name) {
    return withPk(name, Object.class, "uuid");
  }

  public static PrimaryKey withPk(TableConstraintColumn column) {
    PrimaryKey pk = mock(PrimaryKey.class);
    lenient().when(pk.getColumns()).thenReturn(singletonList(column));
    return pk;
  }

  private static PrimaryKey withPk(String name, Class<?> clazz, String typeName) {
    PrimaryKey pk = mock(PrimaryKey.class);
    TableConstraintColumn c = withColumn(TableConstraintColumn.class, name, clazz, typeName, null);
    lenient().when(pk.getColumns()).thenReturn(singletonList(c));
    return pk;
  }

  public static Catalog newCatalog(Table... tables) {
    var catalog = mock(Catalog.class);
    return override(catalog, tables);
  }

  public static AsyncData emptyAsyncData() {
    return new AsyncData(new HashSet<>(), new HashSet<>());
  }

  public static AsyncData fullAsyncData() {
    Set<String> tables = new HashSet<>(List.of(ContextTestUtils.TABLE_NAME));
    Set<String> searchConditions = new HashSet<>(List.of(ContextTestUtils.VIEW_NAME.concat("_v")));
    return new AsyncData(tables, searchConditions);
  }

  public static Catalog override(Catalog catalog, Table... tables) {
    if (isNotEmpty(tables)) {
      given(catalog.getTables()).willReturn(asList(tables));
    }
    return catalog;
  }

  public static Table withTable(String name, PrimaryKey pk) {
    return withTable(name, pk, new Column[0]);
  }

  public static Table withTable(String name, Column... columns) {
    return withTable(name, null, columns);
  }

  public static Table withTable(String name, PrimaryKey pk, Column... columns) {
    var table = table(name, pk, columns);
    lenient().when(table.getTableType()).thenReturn(new TableType("TABLE"));
    return table;
  }

  public static Table withView(String name, Column... columns) {
    var table = table(name, columns);
    lenient().when(table.getTableType()).thenReturn(new TableType("VIEW"));
    return table;
  }

  public static Table withSearchConditionView(String name, Column... columns) {
    var table = table(name + "_v", columns);
    lenient().when(table.getTableType()).thenReturn(new TableType("VIEW"));
    return table;
  }

  public static Table withM2MView(String name, Column... columns) {
    var table = table(name + "_rel_v", columns);
    lenient().when(table.getTableType()).thenReturn(new TableType("VIEW"));
    return table;
  }

  private static Table table(String name, Column... columns) {
    return table(name, null, columns);
  }

  private static Table table(String name, PrimaryKey pk, Column... columns) {
    var table = mock(Table.class);
    lenient().when(table.getName()).thenReturn(name);

    List<Column> columnsList = newArrayList(columns);
    if (pk != null) {
      lenient().when(table.getPrimaryKey()).thenReturn(pk);
      lenient().when(pk.getParent()).thenReturn(table);
      lenient().when(pk.getColumns().get(0).getParent()).thenReturn(table);
      columnsList.add(pk.getColumns().get(0));
    }

    if (isNotEmpty(columns)) {
      lenient().when(table.getColumns()).thenReturn(columnsList);
      Arrays.stream(columns)
          .forEach(column -> lenient().when(column.getParent()).thenReturn(table));
    }

    return table;
  }

  public static Column withTextColumn(String name) {
    return withColumn(name, String.class, "text");
  }

  public static Column withFkColumn(String name, Column referencedColumn) {
    return withColumn(name, Object.class, "uuid", referencedColumn);
  }

  public static Column withUuidColumn(String name) {
    return withColumn(name, Object.class, "uuid");
  }

  public static Column withLocalDateTimeColumn(String name) {
    return withColumn(name, Timestamp.class, "timestampz");
  }

  public static Column withEnumColumn(String name) {
    return withColumn(name, String.class, "en_status");
  }

  public static Column withColumn(String name, Class<?> clazz, String typeName) {
    return withColumn(Column.class, name, clazz, typeName, null);
  }

  public static Column withColumn(String name, Class<?> clazz, String typeName, Column referencedColumn) {
    return withColumn(Column.class, name, clazz, typeName, referencedColumn);
  }

  public static TableConstraintColumn withTableConstraintColumn(String name, Class<?> clazz, String typeName) {
    return withColumn(TableConstraintColumn.class, name, clazz, typeName, null);
  }

  public static Column withColumn(String name, Class<?> clazz, String typeName, int size) {
    return withColumn(Column.class, name, clazz, typeName, null, size);
  }

  private static <T extends Column> T withColumn(
      Class<T> mockClazz, String name, Class<?> clazz, String typeName, Column referencedColumn) {
    var column = mock(mockClazz);
    lenient().when(column.getName()).thenReturn(name);

    var type = mock(ColumnDataType.class);
    lenient().doReturn(clazz).when(type).getTypeMappedClass();
    lenient().when(type.getName()).thenReturn(typeName);

    lenient().when(column.getColumnDataType()).thenReturn(type);
    lenient().when(column.getReferencedColumn()).thenReturn(referencedColumn);
    return column;
  }

  private static <T extends Column> T withColumn(
      Class<T> mockClazz, String name, Class<?> clazz, String typeName, Column referencedColumn, int size) {
    var column = mock(mockClazz);
    lenient().when(column.getName()).thenReturn(name);

    var type = mock(ColumnDataType.class);
    lenient().doReturn(clazz).when(type).getTypeMappedClass();
    lenient().when(type.getName()).thenReturn(typeName);

    lenient().when(column.getColumnDataType()).thenReturn(type);
    lenient().when(column.getReferencedColumn()).thenReturn(referencedColumn);

    lenient().when(column.getSize()).thenReturn(size);
    return column;
  }
}
