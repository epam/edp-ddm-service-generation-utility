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

import com.epam.digital.data.platform.generator.metadata.EnumProvider;
import com.epam.digital.data.platform.generator.metadata.NestedReadEntity;
import com.epam.digital.data.platform.generator.metadata.NestedReadProvider;
import com.epam.digital.data.platform.generator.metadata.SearchConditionPaginationType;
import com.epam.digital.data.platform.generator.metadata.SearchConditionProvider;
import com.epam.digital.data.platform.generator.metadata.SearchConditions;
import com.epam.digital.data.platform.generator.metadata.SearchConditionsBuilder;
import com.epam.digital.data.platform.generator.model.AsyncData;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.model.template.SearchConditionField;
import com.epam.digital.data.platform.generator.model.template.SelectableField;
import com.epam.digital.data.platform.generator.scope.SearchHandlerScope;
import com.epam.digital.data.platform.generator.utils.ContextTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import schemacrawler.schema.Catalog;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.GEOMETRY_COLUMN_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.PK_COLUMN_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.newCatalog;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.override;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withColumn;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withEnumColumn;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withSearchConditionView;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withTable;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withTextColumn;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withUuidColumn;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withUuidPk;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchHandlerScopeFactoryTest {

  private static final String VIEW_NAME = "test_view";
  private static final String TABLE_NAME = "some_table";
  private static final String FIELD = "field";
  private static final String FIELD_TOO = "fieldToo";
  private static final String FIELD_TOO_COLUMN_NAME = "field_too";
  private static final String MANY_TO_MANY_COLUMN = "m2m_column";
  private static final String ONE_TO_MANY_COLUMN = "o2m_column";

  SearchHandlerScopeFactory instance;

  @Mock
  SearchConditionProvider scProvider;
  @Mock
  EnumProvider enumProvider;
  @Mock
  NestedReadProvider nestedReadProvider;

  Context ctx;

  @BeforeEach
  void setup() {
    instance = new SearchHandlerScopeFactory(scProvider, enumProvider, nestedReadProvider);

    Catalog catalog = newCatalog(
        withTable(TABLE_NAME),
        withSearchConditionView(VIEW_NAME,
            withTextColumn(FIELD), withTextColumn(FIELD_TOO_COLUMN_NAME),
            withColumn(GEOMETRY_COLUMN_NAME, Object.class, "geometry")),
        withSearchConditionView("view_without_fields"),
        withSearchConditionView("find_by_enum", withEnumColumn("status"))
    );
    AsyncData asyncData = ContextTestUtils.emptyAsyncData();
    ctx = new Context(null, catalog, asyncData);

    lenient().when(scProvider.findFor(VIEW_NAME)).thenReturn(emptySc());
    lenient().when(scProvider.findFor("view_without_fields")).thenReturn(emptySc());
    lenient().when(scProvider.findFor("find_by_enum")).thenReturn(emptySc());
  }

  private void setupSearchConditions(String view, SearchConditions sc) {
    given(scProvider.findFor(view)).willReturn(sc);
  }

  private SearchConditions emptySc() {
    return new SearchConditionsBuilder().build();
  }

  @Test
  void shouldCreateScopes() {
    lenient().when(scProvider.findFor(VIEW_NAME))
             .thenReturn(new SearchConditionsBuilder()
             .returningColumns(Arrays.asList(FIELD, FIELD_TOO_COLUMN_NAME)).build());

    List<SearchHandlerScope> resultList = instance.create(ctx);

    assertThat(resultList).hasSize(3);
    SearchHandlerScope resultScope = resultList.get(0);
    assertThat(resultScope.getClassName()).isEqualTo("TestViewSearchHandler");
    assertThat(resultScope.getSchemaName()).isEqualTo("TestView");
    assertThat(resultScope.getTableName()).isEqualTo("test_view_v");

    assertThat(resultScope.getSimpleSelectableFields()).hasSize(2);
    assertThat(resultScope.getSimpleSelectableFields().get(0).getName()).isEqualTo(FIELD);
    assertThat(resultScope.getSimpleSelectableFields().get(0).getConverter()).isNull();
    assertThat(resultScope.getSimpleSelectableFields().get(1).getName()).isEqualTo(FIELD_TOO_COLUMN_NAME);
    assertThat(resultScope.getSimpleSelectableFields().get(1).getConverter()).isNull();

    assertThat(resultScope.getNestedListSelectableGroups()).isEmpty();
    assertThat(resultScope.getNestedSingleSelectableGroups()).isEmpty();
  }

  @Test
  void shouldCreateScopeWithNestedRead() {
    override(ctx.getCatalog(), withTable(TABLE_NAME, withUuidPk(PK_COLUMN_NAME), withTextColumn(FIELD)),
            withSearchConditionView(
                    VIEW_NAME,
                    withTextColumn(FIELD),
                    withTextColumn(FIELD_TOO_COLUMN_NAME),
                    withColumn(ONE_TO_MANY_COLUMN, UUID.class, "uuid"),
                    withColumn(MANY_TO_MANY_COLUMN, Object.class, "_uuid")));
    lenient()
        .when(scProvider.findFor(VIEW_NAME))
        .thenReturn(
            new SearchConditionsBuilder()
                .returningColumns(
                    Arrays.asList(
                        FIELD, FIELD_TOO_COLUMN_NAME, ONE_TO_MANY_COLUMN, MANY_TO_MANY_COLUMN))
                .build());
    when(nestedReadProvider.findFor(VIEW_NAME))
        .thenReturn(
            Map.of(
                ONE_TO_MANY_COLUMN,
                new NestedReadEntity(VIEW_NAME, ONE_TO_MANY_COLUMN, TABLE_NAME),
                MANY_TO_MANY_COLUMN,
                new NestedReadEntity(VIEW_NAME, MANY_TO_MANY_COLUMN, TABLE_NAME)));

    List<SearchHandlerScope> resultList = instance.create(ctx);

    assertThat(resultList).hasSize(1);
    SearchHandlerScope resultScope = resultList.get(0);
    assertThat(resultScope.getClassName()).isEqualTo("TestViewSearchHandler");
    assertThat(resultScope.getSchemaName()).isEqualTo("TestView");
    assertThat(resultScope.getTableName()).isEqualTo(VIEW_NAME + "_v");

    assertThat(resultScope.getSimpleSelectableFields()).hasSize(2);
    assertThat(resultScope.getSimpleSelectableFields().get(0).getName()).isEqualTo(FIELD);
    assertThat(resultScope.getSimpleSelectableFields().get(0).getConverter()).isNull();
    assertThat(resultScope.getSimpleSelectableFields().get(1).getName())
        .isEqualTo(FIELD_TOO_COLUMN_NAME);
    assertThat(resultScope.getSimpleSelectableFields().get(1).getConverter()).isNull();

    assertThat(resultScope.getNestedSingleSelectableGroups()).containsOnlyKeys(ONE_TO_MANY_COLUMN);
    assertThat(resultScope.getNestedSingleSelectableGroups().get(ONE_TO_MANY_COLUMN).getTableName())
        .isEqualTo(TABLE_NAME);
    assertThat(resultScope.getNestedSingleSelectableGroups().get(ONE_TO_MANY_COLUMN).getPkName())
        .isEqualTo(PK_COLUMN_NAME);
    assertThat(resultScope.getNestedSingleSelectableGroups().get(ONE_TO_MANY_COLUMN).getFields())
        .usingRecursiveFieldByFieldElementComparator()
        .containsOnly(
            new SelectableField(PK_COLUMN_NAME, TABLE_NAME, null),
            new SelectableField(FIELD, TABLE_NAME, null));

    assertThat(resultScope.getNestedListSelectableGroups()).containsOnlyKeys(MANY_TO_MANY_COLUMN);
    assertThat(resultScope.getNestedListSelectableGroups().get(MANY_TO_MANY_COLUMN).getTableName())
        .isEqualTo(TABLE_NAME);
    assertThat(resultScope.getNestedListSelectableGroups().get(MANY_TO_MANY_COLUMN).getPkName())
        .isEqualTo(PK_COLUMN_NAME);
    assertThat(resultScope.getNestedListSelectableGroups().get(MANY_TO_MANY_COLUMN).getFields())
        .usingRecursiveFieldByFieldElementComparator()
        .containsOnly(
            new SelectableField(PK_COLUMN_NAME, TABLE_NAME, null),
            new SelectableField(FIELD, TABLE_NAME, null));
  }

  @Test
  void shouldNotCreateNestedReadScopeIfColumnNonReturning() {
    override(ctx.getCatalog(), withTable(TABLE_NAME, withUuidPk(PK_COLUMN_NAME), withTextColumn(FIELD)),
            withSearchConditionView(
                    VIEW_NAME,
                    withTextColumn(FIELD),
                    withTextColumn(FIELD_TOO_COLUMN_NAME),
                    withColumn(ONE_TO_MANY_COLUMN, UUID.class, "uuid"),
                    withColumn(MANY_TO_MANY_COLUMN, Object.class, "_uuid")));
    lenient()
        .when(scProvider.findFor(VIEW_NAME))
        .thenReturn(new SearchConditionsBuilder().returningColumns(List.of(FIELD)).build());
    when(nestedReadProvider.findFor(VIEW_NAME))
            .thenReturn(
                    Map.of(
                            ONE_TO_MANY_COLUMN,
                            new NestedReadEntity(VIEW_NAME, ONE_TO_MANY_COLUMN, TABLE_NAME),
                            MANY_TO_MANY_COLUMN,
                            new NestedReadEntity(VIEW_NAME, MANY_TO_MANY_COLUMN, TABLE_NAME)));

    List<SearchHandlerScope> resultList = instance.create(ctx);

    assertThat(resultList).hasSize(1);
    SearchHandlerScope resultScope = resultList.get(0);

    assertThat(resultScope.getNestedSingleSelectableGroups()).isEmpty();
    assertThat(resultScope.getNestedListSelectableGroups()).isEmpty();
  }

  @Test
  void shouldCreateContainsFields() {
    // given
    var field = new SearchConditionField(FIELD, FIELD, true);
    var fieldToo = new SearchConditionField(FIELD_TOO, FIELD_TOO_COLUMN_NAME, true);

    setupSearchConditions(VIEW_NAME, new SearchConditionsBuilder()
        .contains(List.of(field.getColumnName(), fieldToo.getColumnName()))
        .build());

    // when
    List<SearchHandlerScope> resultList = instance.create(ctx);

    // then
    List<SearchConditionField> fields = resultList.get(0).getContainsFields();
    assertThat(fields).hasSize(2);
    assertThat(fields.get(0)).usingRecursiveComparison().isEqualTo(field);
    assertThat(fields.get(1)).usingRecursiveComparison().isEqualTo(fieldToo);
  }

  @Test
  void shouldCreateStartsWithFields() {
    // given
    var field = new SearchConditionField(FIELD, FIELD, true);
    var fieldToo = new SearchConditionField(FIELD_TOO, FIELD_TOO_COLUMN_NAME, true);

    setupSearchConditions(VIEW_NAME, new SearchConditionsBuilder()
        .startsWith(List.of(field.getColumnName(), fieldToo.getColumnName()))
        .build());

    // when
    List<SearchHandlerScope> resultList = instance.create(ctx);

    // then
    List<SearchConditionField> fields = resultList.get(0).getStartsWithFields();
    assertThat(fields).hasSize(2);
    assertThat(fields.get(0)).usingRecursiveComparison().isEqualTo(field);
    assertThat(fields.get(1)).usingRecursiveComparison().isEqualTo(fieldToo);
  }

  @Test
  void shouldCreateStartsWithArrayFields() {
    // given
    var field = new SearchConditionField(FIELD, FIELD, true);
    var fieldToo = new SearchConditionField(FIELD_TOO, FIELD_TOO_COLUMN_NAME, true);

    setupSearchConditions(VIEW_NAME, new SearchConditionsBuilder()
            .startsWithArray(List.of(field.getColumnName(), fieldToo.getColumnName()))
            .build());

    // when
    List<SearchHandlerScope> resultList = instance.create(ctx);

    // then
    List<SearchConditionField> fields = resultList.get(0).getStartsWithArrayFields();
    assertThat(fields).hasSize(2);
    assertThat(fields.get(0)).usingRecursiveComparison().isEqualTo(field);
    assertThat(fields.get(1)).usingRecursiveComparison().isEqualTo(fieldToo);
  }

  @Test
  void shouldCreateInFields() {
    // given
    var field = new SearchConditionField(FIELD, FIELD, true);
    var fieldToo = new SearchConditionField(FIELD_TOO, FIELD_TOO_COLUMN_NAME, true);

    setupSearchConditions(VIEW_NAME, new SearchConditionsBuilder()
            .in(List.of(field.getColumnName(), fieldToo.getColumnName()))
            .build());

    // when
    List<SearchHandlerScope> resultList = instance.create(ctx);

    // then
    List<SearchConditionField> fields = resultList.get(0).getInFields();
    assertThat(fields).hasSize(2);
    assertThat(fields.get(0)).usingRecursiveComparison().isEqualTo(field);
    assertThat(fields.get(1)).usingRecursiveComparison().isEqualTo(fieldToo);
  }

  @Test
  void shouldCreateNotInFields() {
    // given
    var field = new SearchConditionField(FIELD, FIELD, true);
    var fieldToo = new SearchConditionField(FIELD_TOO, FIELD_TOO_COLUMN_NAME, true);

    setupSearchConditions(VIEW_NAME, new SearchConditionsBuilder()
        .notIn(List.of(field.getColumnName(), fieldToo.getColumnName()))
        .build());

    // when
    List<SearchHandlerScope> resultList = instance.create(ctx);

    // then
    List<SearchConditionField> fields = resultList.get(0).getNotInFields();
    assertThat(fields).hasSize(2);
    assertThat(fields.get(0)).usingRecursiveComparison().isEqualTo(field);
    assertThat(fields.get(1)).usingRecursiveComparison().isEqualTo(fieldToo);
  }

  @Test
  void shouldCreateBetweenFields() {
    // given
    var field = new SearchConditionField(FIELD, FIELD, true);
    var fieldToo = new SearchConditionField(FIELD_TOO, FIELD_TOO_COLUMN_NAME, true);

    setupSearchConditions(VIEW_NAME, new SearchConditionsBuilder()
            .between(List.of(field.getColumnName(), fieldToo.getColumnName()))
            .build());

    // when
    List<SearchHandlerScope> resultList = instance.create(ctx);

    // then
    List<SearchConditionField> fields = resultList.get(0).getBetweenFields();
    assertThat(fields).hasSize(2);
    assertThat(fields.get(0)).usingRecursiveComparison().isEqualTo(field);
    assertThat(fields.get(1)).usingRecursiveComparison().isEqualTo(fieldToo);
  }

  @Test
  void shouldCreateNotEqualFields() {
    // given
    var field = new SearchConditionField(FIELD, FIELD, true);
    var fieldToo = new SearchConditionField(FIELD_TOO, FIELD_TOO_COLUMN_NAME, true);

    setupSearchConditions(VIEW_NAME, new SearchConditionsBuilder()
        .notEqual(List.of(field.getColumnName(), fieldToo.getColumnName()))
        .build());

    // when
    List<SearchHandlerScope> resultList = instance.create(ctx);

    // then
    List<SearchConditionField> fields = resultList.get(0).getNotEqualFields();
    assertThat(fields).hasSize(2);
    assertThat(fields.get(0)).usingRecursiveComparison().isEqualTo(field);
    assertThat(fields.get(1)).usingRecursiveComparison().isEqualTo(fieldToo);
  }

  @Test
  void shouldCreateEqualFieldsForTextColumns() {
    // given
    var field = new SearchConditionField(FIELD, FIELD, true);
    var fieldToo = new SearchConditionField(FIELD_TOO, FIELD_TOO_COLUMN_NAME, true);

    setupSearchConditions(VIEW_NAME, new SearchConditionsBuilder()
        .equal(List.of(field.getColumnName(), fieldToo.getColumnName()))
        .build());

    // when
    List<SearchHandlerScope> resultList = instance.create(ctx);

    // then
    List<SearchConditionField> fields = resultList.get(0).getEqualFields();
    assertThat(fields).hasSize(2);
    assertThat(fields.get(0)).usingRecursiveComparison().isEqualTo(field);
    assertThat(fields.get(1)).usingRecursiveComparison().isEqualTo(fieldToo);
  }

  @Test
  void shouldCreateEqualFieldsForNonStringColumns() {
    // given
    var field = new SearchConditionField(FIELD, FIELD, false);
    var fieldToo = new SearchConditionField(FIELD_TOO, FIELD_TOO_COLUMN_NAME, false);

    setupSearchConditions(VIEW_NAME, new SearchConditionsBuilder()
        .equal(List.of(field.getColumnName(), fieldToo.getColumnName()))
        .build());

    override(ctx.getCatalog(),
        withTable(TABLE_NAME),
        withSearchConditionView(VIEW_NAME,
            withUuidColumn(FIELD), withUuidColumn(FIELD_TOO_COLUMN_NAME)),
        withSearchConditionView("view_without_fields"));

    // when
    List<SearchHandlerScope> resultList = instance.create(ctx);

    //then
    List<SearchConditionField> fields = resultList.get(0).getEqualFields();
    assertThat(fields).hasSize(2);
    assertThat(fields.get(0)).usingRecursiveComparison().isEqualTo(field);
    assertThat(fields.get(1)).usingRecursiveComparison().isEqualTo(fieldToo);
  }

  @Test
  void shouldThrowExceptionWhenEqualFieldsIsNotFoundInTable() {
    // given
    setupSearchConditions("view_without_fields", new SearchConditionsBuilder()
        .equal(List.of(FIELD, FIELD_TOO))
        .build());

    // when-then
    assertThatThrownBy(() -> instance.create(ctx)).isInstanceOf(IllegalStateException.class);
  }

  @Test
  void shouldSetLimitWhenPresent() {
    int limit = 1;
    setupSearchConditions(VIEW_NAME, new SearchConditionsBuilder().limit(limit).build());

    List<SearchHandlerScope> resultList = instance.create(ctx);

    assertThat(resultList.get(0).getLimit()).isEqualTo(limit);
  }

  @Test
  void shouldSetNullLimitWhenNotPresent() {
    setupSearchConditions(VIEW_NAME, emptySc());

    List<SearchHandlerScope> resultList = instance.create(ctx);

    assertThat(resultList.get(0).getLimit()).isNull();
  }

  @Test
  void shouldSetPaginationWhenPresent() {
    setupSearchConditions(
        VIEW_NAME,
        new SearchConditionsBuilder().pagination(SearchConditionPaginationType.TRUE).build());

    List<SearchHandlerScope> resultList = instance.create(ctx);

    assertThat(resultList.get(0).getPagination()).isEqualTo(SearchConditionPaginationType.TRUE);
  }

  @Test
  void shouldSetNullPaginationWhenNotPresent() {
    setupSearchConditions(VIEW_NAME, emptySc());

    List<SearchHandlerScope> resultList = instance.create(ctx);

    assertThat(resultList.get(0).getPagination()).isNull();
  }

  @Test
  void shouldCreateEqualFieldsForEnumColumns() {
    // given
    when(enumProvider.findFor("en_status")).thenReturn(Set.of("en_status"));
    when(enumProvider.findAllWithValues())
        .thenReturn(Map.of("en_status", List.of("ACCEPTED", "PENDING", "REJECTED")));

    var field = new SearchConditionField("status", "status", false);

    setupSearchConditions("find_by_enum", new SearchConditionsBuilder()
        .equal(List.of(field.getColumnName()))
        .build());

    override(ctx.getCatalog(),
        withTable(TABLE_NAME),
        withSearchConditionView("find_by_enum", withEnumColumn("status")));

    // when
    List<SearchHandlerScope> resultList = instance.create(ctx);

    //then
    List<SearchConditionField> fields = resultList.get(0).getEqualFields();
    assertThat(fields).hasSize(1);
    assertThat(fields.get(0)).usingRecursiveComparison().isEqualTo(field);
    assertThat(resultList.get(0).getEnumSearchConditionFields()).containsExactly("status");
  }

  @Test
  void shouldCreateInFieldsForEnumColumns() {
    // given
    when(enumProvider.findFor("en_status")).thenReturn(Set.of("en_status"));
    when(enumProvider.findAllWithValues())
            .thenReturn(Map.of("en_status", List.of("ACCEPTED", "PENDING", "REJECTED")));

    var field = new SearchConditionField("status", "status", false);

    setupSearchConditions("find_by_enum", new SearchConditionsBuilder()
            .in(List.of(field.getColumnName()))
            .build());

    override(ctx.getCatalog(),
            withTable(TABLE_NAME),
            withSearchConditionView("find_by_enum", withEnumColumn("status")));

    // when
    List<SearchHandlerScope> resultList = instance.create(ctx);

    //then
    List<SearchConditionField> fields = resultList.get(0).getInFields();
    assertThat(fields).hasSize(1);
    assertThat(fields.get(0)).usingRecursiveComparison().isEqualTo(field);
  }

  @Test
  void shouldCreateBetweenFieldsForEnumColumns() {
    // given
    when(enumProvider.findFor("en_status")).thenReturn(Set.of("en_status"));
    when(enumProvider.findAllWithValues())
            .thenReturn(Map.of("en_status", List.of("ACCEPTED", "PENDING", "REJECTED")));

    var field = new SearchConditionField("status", "status", false);

    setupSearchConditions("find_by_enum", new SearchConditionsBuilder()
            .between(List.of(field.getColumnName()))
            .build());

    override(ctx.getCatalog(),
            withTable(TABLE_NAME),
            withSearchConditionView("find_by_enum", withEnumColumn("status")));

    // when
    List<SearchHandlerScope> resultList = instance.create(ctx);

    //then
    List<SearchConditionField> fields = resultList.get(0).getBetweenFields();
    assertThat(fields).hasSize(1);
    assertThat(fields.get(0)).usingRecursiveComparison().isEqualTo(field);
  }
}
