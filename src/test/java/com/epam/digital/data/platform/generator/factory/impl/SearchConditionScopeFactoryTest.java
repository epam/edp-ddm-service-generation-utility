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

import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.newCatalog;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.override;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withColumn;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withSearchConditionView;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withTable;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withTextColumn;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;

import com.epam.digital.data.platform.generator.constraints.impl.CompositeConstraintProvider;
import com.epam.digital.data.platform.generator.constraints.impl.FormattingConstraintProvider;
import com.epam.digital.data.platform.generator.constraints.impl.MarshalingConstraintProvider;
import com.epam.digital.data.platform.generator.metadata.EnumProvider;
import com.epam.digital.data.platform.generator.metadata.SearchConditionPaginationType;
import com.epam.digital.data.platform.generator.metadata.SearchConditionProvider;
import com.epam.digital.data.platform.generator.metadata.SearchConditions;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.model.template.Constraint.Content;
import com.epam.digital.data.platform.generator.model.template.Field;
import com.epam.digital.data.platform.generator.model.template.SearchType;
import com.epam.digital.data.platform.generator.scope.ModelScope;
import com.epam.digital.data.platform.generator.utils.ContextTestUtils;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SearchConditionScopeFactoryTest {

  @Mock
  private SearchConditionProvider searchConditionProvider;

  @Mock
  private EnumProvider enumProvider;

  @Mock
  private CompositeConstraintProvider constraintProviders;

  @Mock
  private FormattingConstraintProvider formattingConstraintProvider;

  @Mock
  private MarshalingConstraintProvider marshalingConstraintProvider;

  private SearchConditionScopeFactory instance;

  private Context context;

  @BeforeEach
  public void init() {
    instance = new SearchConditionScopeFactory(searchConditionProvider, enumProvider,
        constraintProviders);

    lenient().when(constraintProviders.getFormattingConstraintProvider())
        .thenReturn(formattingConstraintProvider);
    lenient().when(constraintProviders.getMarshalingConstraintProvider())
        .thenReturn(marshalingConstraintProvider);

    context = new Context(null,
        newCatalog(withTable("test_table"), withTable("second_test_table")),
        ContextTestUtils.emptyAsyncData());
  }

  @Test
  void shouldCreateEntityScopesForSearchConditionViews() {
    given(searchConditionProvider.findFor(any())).willReturn(new SearchConditions());
    override(
        context.getCatalog(),
        withSearchConditionView("test_table_search"),
        withSearchConditionView("second_test_table_search"));

    List<String> result = toNamesList(instance.create(context));

    assertThat(result)
        .contains("TestTableSearchSearchConditions", "SecondTestTableSearchSearchConditions");
  }

  @Test
  void shouldCreateSearchConditionScopesForSearchConditionViews() {
    given(searchConditionProvider.findFor(any())).willReturn(new SearchConditions());
    override(
        context.getCatalog(),
        withSearchConditionView("test_table_search"),
        withSearchConditionView("second_test_table_search"));

    List<String> result = toNamesList(instance.create(context));

    assertThat(result)
        .contains("TestTableSearchSearchConditions", "SecondTestTableSearchSearchConditions");
  }

  @Test
  void shouldCreateSearchConditionFieldsForSearchConditionViews() {
    var scInfo = new SearchConditions();
    scInfo.setColumnToSearchType(Map.of("test_search_col", SearchType.CONTAINS));
    var scInfo2 = new SearchConditions();
    scInfo2.setColumnToSearchType(Map.of("test_2nd_search_col", SearchType.CONTAINS));
    given(searchConditionProvider.findFor(any()))
        .willReturn(scInfo, scInfo2);
    override(
        context.getCatalog(),
        withSearchConditionView("test_table_search", withTextColumn("test_search_col")),
        withSearchConditionView("second_test_table_search", withTextColumn("test_2nd_search_col")));

    List<ModelScope> result = instance.create(context).stream()
        .filter(x -> x.getClassName().equals("TestTableSearchSearchConditions"))
        .collect(toList());

    var fields = result.get(0).getFields();
    assertThat(fields).hasSize(1);

    var field = fields.iterator().next();
    var contentValues = field.getConstraints().stream()
        .flatMap(cont -> cont.getContent().stream())
        .map(Content::getValue).collect(toList());

    assertThat(field.getName()).isEqualTo("testSearchCol");
    assertThat(field.getType()).isEqualTo("java.lang.String");
  }

  @Test
  void shouldCreateSearchConditionInFieldsForSearchConditionViews() {
    var scInfo = new SearchConditions();
    scInfo.setColumnToSearchType(Map.of("test_in_search_col", SearchType.IN));
    given(searchConditionProvider.findFor(any()))
        .willReturn(scInfo);
    override(
        context.getCatalog(),
        withSearchConditionView("test_table_search", withTextColumn("test_in_search_col")));

    List<ModelScope> result = instance.create(context);

    var fields = result.get(0).getFields();
    assertThat(fields).hasSize(1);

    var field = fields.iterator().next();
    var contentValues = field.getConstraints().stream()
        .flatMap(cont -> cont.getContent().stream())
        .map(Content::getValue).collect(toList());

    assertThat(field.getName()).isEqualTo("testInSearchCol");
    assertThat(field.getType()).isEqualTo("java.util.List<java.lang.String>");
    assertThat(contentValues).contains("com.fasterxml.jackson.annotation.JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY");
  }

  @Test
  void shouldCreateSearchConditionNotInFieldsForSearchConditionViews() {
    var scInfo = new SearchConditions();
    scInfo.setColumnToSearchType(Map.of("test_not_in_search_col", SearchType.NOT_IN));
    given(searchConditionProvider.findFor(any())).willReturn(scInfo);
    override(
        context.getCatalog(),
        withSearchConditionView("test_table_search", withTextColumn("test_not_in_search_col")));

    List<ModelScope> result = instance.create(context);

    var fields = result.get(0).getFields();
    assertThat(fields).hasSize(1);

    var field = fields.iterator().next();
    var contentValues = field.getConstraints().stream()
        .flatMap(cont -> cont.getContent().stream())
        .map(Content::getValue).collect(toList());

    assertThat(field.getName()).isEqualTo("testNotInSearchCol");
    assertThat(field.getType()).isEqualTo("java.util.List<java.lang.String>");
    assertThat(contentValues).contains("com.fasterxml.jackson.annotation.JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY");
  }

  @Test
  void shouldCreateSearchConditionNotEqualFieldsForSearchConditionViews() {
    var scInfo = new SearchConditions();
    scInfo.setColumnToSearchType(Map.of("test_not_equal_search_col", SearchType.NOT_IN));
    given(searchConditionProvider.findFor(any())).willReturn(scInfo);
    override(
        context.getCatalog(),
        withSearchConditionView("test_table_search", withTextColumn("test_not_equal_search_col")));

    List<ModelScope> result = instance.create(context);

    var fields = result.get(0).getFields();
    assertThat(fields).hasSize(1);

    var field = fields.iterator().next();
    assertThat(field.getName()).isEqualTo("testNotEqualSearchCol");
    assertThat(field.getType()).isEqualTo("java.util.List<java.lang.String>");
  }

  @Test
  void shouldCreateSearchConditionBetweenFieldsForSearchConditionViews() {
    var scInfo = new SearchConditions();
    scInfo.setColumnToSearchType(Map.of("test_between_search_col", SearchType.BETWEEN));
    given(searchConditionProvider.findFor(any()))
            .willReturn(scInfo);
    override(
            context.getCatalog(),
            withSearchConditionView("test_table_search", withTextColumn("test_between_search_col")));

    List<ModelScope> result = instance.create(context);

    var fields = result.get(0).getFields();
    assertThat(fields)
        .hasSize(2)
        .containsExactlyInAnyOrder(
            new Field("java.lang.String", "testBetweenSearchColFrom", emptyList()),
            new Field("java.lang.String", "testBetweenSearchColTo", emptyList()));
  }

  @Test
  void shouldCreateEnumFieldForSearchConditionView() {
    Field expected = new Field("EnStatus", "status", emptyList());
    override(
        context.getCatalog(),
        withSearchConditionView("test_table",
            withColumn("status", String.class, "en_status")));
    given(enumProvider.findFor("en_status")).willReturn(Set.of("en_status"));
    var scInfo = new SearchConditions();
    scInfo.setColumnToSearchType(Map.of("status", SearchType.EQUAL));
    given(searchConditionProvider.findFor(any())).willReturn(scInfo);

    List<ModelScope> scopes = instance.create(context);

    ModelScope scope = scopes.get(0);
    assertThat(scope.getFields())
        .usingFieldByFieldElementComparator()
        .containsExactly(expected);
  }

  @Test
  void shouldCreateLimitOffsetFieldsForSearchConditionView() {
    Field limit = new Field("java.lang.Integer", "limit", emptyList());
    Field offset = new Field("java.lang.Integer", "offset", emptyList());
    override(
        context.getCatalog(),
        withSearchConditionView("test_table",
            withColumn("status", String.class, "en_status")));

    var scInfo = new SearchConditions();
    scInfo.setPagination(SearchConditionPaginationType.OFFSET);
    given(searchConditionProvider.findFor(any())).willReturn(scInfo);

    List<ModelScope> result = instance.create(context).stream()
        .filter(x -> x.getClassName().equals("TestTableSearchConditions"))
        .collect(toList());

    var fields = result.get(0).getFields();
    assertThat(fields).hasSize(2);

    assertThat(fields)
        .usingFieldByFieldElementComparator()
        .contains(limit, offset);
  }

  @Test
  void shouldCreateNoPaginationFieldsForSearchConditionView() {
    override(
            context.getCatalog(),
            withSearchConditionView("test_table",
                    withColumn("status", String.class, "en_status")));

    var scInfo = new SearchConditions();
    scInfo.setPagination(SearchConditionPaginationType.NONE);
    given(searchConditionProvider.findFor(any())).willReturn(scInfo);

    List<ModelScope> result = instance.create(context).stream()
            .filter(x -> x.getClassName().equals("TestTableSearchConditions"))
            .collect(toList());

    var fields = result.get(0).getFields();
    assertThat(fields).isEmpty();
  }

  @Test
  void shouldCreatePagingFieldsForSearchConditionView() {
    Field pageSize = new Field("java.lang.Integer", "pageSize", emptyList());
    Field pageNo = new Field("java.lang.Integer", "pageNo", emptyList());
    override(
            context.getCatalog(),
            withSearchConditionView("test_table",
                    withColumn("status", String.class, "en_status")));

    var scInfo = new SearchConditions();
    scInfo.setPagination(SearchConditionPaginationType.PAGE);
    given(searchConditionProvider.findFor(any())).willReturn(scInfo);

    List<ModelScope> result = instance.create(context).stream()
            .filter(x -> x.getClassName().equals("TestTableSearchConditions"))
            .collect(toList());

    var fields = result.get(0).getFields();
    assertThat(fields).hasSize(2);

    assertThat(fields)
            .usingFieldByFieldElementComparator()
            .contains(pageSize, pageNo);
  }

  @Test
  void shouldMapEnumFieldToStringForStartsWithSearch() {
    Field expected = new Field(String.class.getCanonicalName(), "status", emptyList());
    override(
        context.getCatalog(),
        withSearchConditionView("test_table",
            withColumn("status", String.class, "en_status")));
    given(enumProvider.findFor("en_status")).willReturn(Set.of("en_status"));
    var scInfo = new SearchConditions();
    scInfo.setColumnToSearchType(Map.of("status", SearchType.STARTS_WITH));
    given(searchConditionProvider.findFor(any())).willReturn(scInfo);

    List<ModelScope> scopes = instance.create(context);

    ModelScope scope = scopes.get(0);
    assertThat(scope.getFields())
        .usingFieldByFieldElementComparator()
        .containsExactly(expected);
  }

  @Test
  void shouldMapEnumFieldToStringForContainsSearch() {
    Field expected = new Field(String.class.getCanonicalName(), "status", emptyList());
    override(
        context.getCatalog(),
        withSearchConditionView("test_table",
            withColumn("status", String.class, "en_status")));
    given(enumProvider.findFor("en_status")).willReturn(Set.of("en_status"));
    var scInfo = new SearchConditions();
    scInfo.setColumnToSearchType(Map.of("status", SearchType.CONTAINS));
    given(searchConditionProvider.findFor(any())).willReturn(scInfo);

    List<ModelScope> scopes = instance.create(context);

    ModelScope scope = scopes.get(0);
    assertThat(scope.getFields())
        .usingFieldByFieldElementComparator()
        .containsExactly(expected);
  }

  private List<String> toNamesList(List<ModelScope> modelScopes) {
    return modelScopes.stream()
        .map(ModelScope::getClassName)
        .collect(toList());
  }
}
