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

import static com.epam.digital.data.platform.generator.factory.impl.SearchHandlerScopeFactory.OP_CONTAINS_IGNORE_CASE;
import static com.epam.digital.data.platform.generator.factory.impl.SearchHandlerScopeFactory.OP_EQ;
import static com.epam.digital.data.platform.generator.factory.impl.SearchHandlerScopeFactory.OP_EQUAL_IGNORE_CASE;
import static com.epam.digital.data.platform.generator.factory.impl.SearchHandlerScopeFactory.OP_STARTS_WITH_IGNORE_CASE;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.newCatalog;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.override;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withEnumColumn;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withSearchConditionView;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withTable;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withTextColumn;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withUuidColumn;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import com.epam.digital.data.platform.generator.metadata.EnumProvider;
import com.epam.digital.data.platform.generator.metadata.SearchConditionProvider;
import com.epam.digital.data.platform.generator.metadata.SearchConditions;
import com.epam.digital.data.platform.generator.metadata.SearchConditionsBuilder;
import com.epam.digital.data.platform.generator.model.AsyncData;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.model.template.SearchConditionField;
import com.epam.digital.data.platform.generator.scope.SearchHandlerScope;

import com.epam.digital.data.platform.generator.utils.ContextTestUtils;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import schemacrawler.schema.Catalog;

@ExtendWith(MockitoExtension.class)
class SearchHandlerScopeFactoryTest {

  private static final String FIELD = "field";
  private static final String FIELD_TOO = "fieldToo";
  private static final String FIELD_TOO_COLUMN_NAME = "field_too";

  SearchHandlerScopeFactory instance;

  @Mock
  SearchConditionProvider scProvider;
  @Mock
  EnumProvider enumProvider;

  Context ctx;

  @BeforeEach
  void setup() {
    instance = new SearchHandlerScopeFactory(scProvider, enumProvider);

    Catalog catalog = newCatalog(
        withTable("some_table"),
        withSearchConditionView("test_view",
            withTextColumn(FIELD), withTextColumn(FIELD_TOO_COLUMN_NAME)),
        withSearchConditionView("view_without_fields"),
        withSearchConditionView("find_by_enum", withEnumColumn("status"))
    );
    AsyncData asyncData = ContextTestUtils.emptyAsyncData();
    ctx = new Context(null, catalog, asyncData);

    lenient().when(scProvider.findFor("test_view")).thenReturn(emptySc());
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
    lenient().when(scProvider.findFor("test_view"))
             .thenReturn(new SearchConditionsBuilder()
             .returningColumns(Arrays.asList(FIELD, FIELD_TOO_COLUMN_NAME)).build());

    List<SearchHandlerScope> resultList = instance.create(ctx);

    assertThat(resultList).hasSize(3);
    SearchHandlerScope resultScope = resultList.get(0);
    assertThat(resultScope.getClassName()).isEqualTo("TestViewSearchHandler");
    assertThat(resultScope.getSchemaName()).isEqualTo("TestView");
    assertThat(resultScope.getTableName()).isEqualTo("test_view_v");

    assertThat(resultScope.getOutputFields()).hasSize(2);
    assertThat(resultScope.getOutputFields().get(0).getName()).isEqualTo(FIELD);
    assertThat(resultScope.getOutputFields().get(0).getConverter()).isNull();
    assertThat(resultScope.getOutputFields().get(1).getName()).isEqualTo(FIELD_TOO_COLUMN_NAME);
    assertThat(resultScope.getOutputFields().get(1).getConverter()).isNull();
  }

  @Test
  void shouldCreateContainsFields() {
    // given
    var field = new SearchConditionField(FIELD, FIELD, OP_CONTAINS_IGNORE_CASE);
    var fieldToo = new SearchConditionField(FIELD_TOO, FIELD_TOO_COLUMN_NAME, OP_CONTAINS_IGNORE_CASE);

    setupSearchConditions("test_view", new SearchConditionsBuilder()
        .contains(List.of(field.getColumnName(), fieldToo.getColumnName()))
        .build());

    // when
    List<SearchHandlerScope> resultList = instance.create(ctx);

    // then
    List<SearchConditionField> fields = resultList.get(0).getSearchConditionFields();
    assertThat(fields).hasSize(2);
    assertThat(fields.get(0)).usingRecursiveComparison().isEqualTo(field);
    assertThat(fields.get(1)).usingRecursiveComparison().isEqualTo(fieldToo);
  }

  @Test
  void shouldCreateStartsWithFields() {
    // given
    var field = new SearchConditionField(FIELD, FIELD, OP_STARTS_WITH_IGNORE_CASE);
    var fieldToo = new SearchConditionField(FIELD_TOO, FIELD_TOO_COLUMN_NAME, OP_STARTS_WITH_IGNORE_CASE);

    setupSearchConditions("test_view", new SearchConditionsBuilder()
        .startsWith(List.of(field.getColumnName(), fieldToo.getColumnName()))
        .build());

    // when
    List<SearchHandlerScope> resultList = instance.create(ctx);

    // then
    List<SearchConditionField> fields = resultList.get(0).getSearchConditionFields();
    assertThat(fields).hasSize(2);
    assertThat(fields.get(0)).usingRecursiveComparison().isEqualTo(field);
    assertThat(fields.get(1)).usingRecursiveComparison().isEqualTo(fieldToo);
  }

  @Test
  void shouldCreateEqualFieldsForTextColumns() {
    // given
    var field = new SearchConditionField(FIELD, FIELD, OP_EQUAL_IGNORE_CASE);
    var fieldToo = new SearchConditionField(FIELD_TOO, FIELD_TOO_COLUMN_NAME, OP_EQUAL_IGNORE_CASE);

    setupSearchConditions("test_view", new SearchConditionsBuilder()
        .equal(List.of(field.getColumnName(), fieldToo.getColumnName()))
        .build());

    // when
    List<SearchHandlerScope> resultList = instance.create(ctx);

    // then
    List<SearchConditionField> fields = resultList.get(0).getSearchConditionFields();
    assertThat(fields).hasSize(2);
    assertThat(fields.get(0)).usingRecursiveComparison().isEqualTo(field);
    assertThat(fields.get(1)).usingRecursiveComparison().isEqualTo(fieldToo);
  }

  @Test
  void shouldCreateEqualFieldsForNonStringColumns() {
    // given
    var field = new SearchConditionField(FIELD, FIELD, OP_EQ);
    var fieldToo = new SearchConditionField(FIELD_TOO, FIELD_TOO_COLUMN_NAME, OP_EQ);

    setupSearchConditions("test_view", new SearchConditionsBuilder()
        .equal(List.of(field.getColumnName(), fieldToo.getColumnName()))
        .build());

    override(ctx.getCatalog(),
        withTable("some_table"),
        withSearchConditionView("test_view",
            withUuidColumn(FIELD), withUuidColumn(FIELD_TOO_COLUMN_NAME)),
        withSearchConditionView("view_without_fields"));

    // when
    List<SearchHandlerScope> resultList = instance.create(ctx);

    //then
    List<SearchConditionField> fields = resultList.get(0).getSearchConditionFields();
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
    setupSearchConditions("test_view", new SearchConditionsBuilder().limit(limit).build());

    List<SearchHandlerScope> resultList = instance.create(ctx);

    assertThat(resultList.get(0).getLimit()).isEqualTo(limit);
  }

  @Test
  void shouldSetNullLimitWhenNotPresent() {
    setupSearchConditions("test_view", emptySc());

    List<SearchHandlerScope> resultList = instance.create(ctx);

    assertThat(resultList.get(0).getLimit()).isEqualTo(null);
  }

  @Test
  void shouldSetPaginationWhenPresent() {
    setupSearchConditions("test_view", new SearchConditionsBuilder().pagination(true).build());

    List<SearchHandlerScope> resultList = instance.create(ctx);

    assertThat(resultList.get(0).getPagination()).isEqualTo(true);
  }

  @Test
  void shouldSetNullPaginationWhenNotPresent() {
    setupSearchConditions("test_view", emptySc());

    List<SearchHandlerScope> resultList = instance.create(ctx);

    assertThat(resultList.get(0).getPagination()).isEqualTo(null);
  }

  @Test
  void shouldCreateEqualFieldsForEnumColumns() {
    // given
    when(enumProvider.findFor("en_status")).thenReturn(Set.of("en_status"));
    when(enumProvider.findAllWithValues())
        .thenReturn(Map.of("en_status", List.of("ACCEPTED", "PENDING", "REJECTED")));

    var field = new SearchConditionField("status", "status", OP_EQ);

    setupSearchConditions("find_by_enum", new SearchConditionsBuilder()
        .equal(List.of(field.getColumnName()))
        .build());

    override(ctx.getCatalog(),
        withTable("some_table"),
        withSearchConditionView("find_by_enum", withEnumColumn("status")));

    // when
    List<SearchHandlerScope> resultList = instance.create(ctx);

    //then
    List<SearchConditionField> fields = resultList.get(0).getSearchConditionFields();
    assertThat(fields).hasSize(1);
    assertThat(fields.get(0)).usingRecursiveComparison().isEqualTo(field);
    assertThat(resultList.get(0).getEnumSearchConditionFields()).containsExactly("status");
  }
}
