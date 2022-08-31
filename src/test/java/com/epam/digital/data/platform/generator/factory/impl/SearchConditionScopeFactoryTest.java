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
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;

import com.epam.digital.data.platform.generator.constraints.impl.CompositeConstraintProvider;
import com.epam.digital.data.platform.generator.constraints.impl.FormattingConstraintProvider;
import com.epam.digital.data.platform.generator.constraints.impl.MarshalingConstraintProvider;
import com.epam.digital.data.platform.generator.metadata.EnumProvider;
import com.epam.digital.data.platform.generator.metadata.SearchConditionProvider;
import com.epam.digital.data.platform.generator.metadata.SearchConditionsBuilder;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.model.template.Field;
import com.epam.digital.data.platform.generator.scope.ModelScope;
import com.epam.digital.data.platform.generator.utils.ContextTestUtils;
import java.util.List;
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
    given(searchConditionProvider.findFor(any())).willReturn(new SearchConditionsBuilder().build());
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
    given(searchConditionProvider.findFor(any())).willReturn(new SearchConditionsBuilder().build());
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
    given(searchConditionProvider.findFor(any()))
        .willReturn(
            new SearchConditionsBuilder().contains(singletonList("test_search_col")).build(),
            new SearchConditionsBuilder().contains(singletonList("test_2nd_search_col")).build());
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
    assertThat(field.getName()).isEqualTo("testSearchCol");
    assertThat(field.getType()).isEqualTo("java.lang.String");
  }

  @Test
  void shouldCreateSearchConditionInFieldsForSearchConditionViews() {
    given(searchConditionProvider.findFor(any()))
        .willReturn(new SearchConditionsBuilder().in(singletonList("test_in_search_col")).build());
    override(
        context.getCatalog(),
        withSearchConditionView("test_table_search", withTextColumn("test_in_search_col")));

    List<ModelScope> result = instance.create(context);

    var fields = result.get(0).getFields();
    assertThat(fields).hasSize(1);

    var field = fields.iterator().next();
    assertThat(field.getName()).isEqualTo("testInSearchCol");
    assertThat(field.getType()).isEqualTo("java.util.List<java.lang.String>");
  }

  @Test
  void shouldCreateSearchConditionNotInFieldsForSearchConditionViews() {
    given(searchConditionProvider.findFor(any()))
        .willReturn(new SearchConditionsBuilder().notIn(singletonList("test_not_in_search_col")).build());
    override(
        context.getCatalog(),
        withSearchConditionView("test_table_search", withTextColumn("test_not_in_search_col")));

    List<ModelScope> result = instance.create(context);

    var fields = result.get(0).getFields();
    assertThat(fields).hasSize(1);

    var field = fields.iterator().next();
    assertThat(field.getName()).isEqualTo("testNotInSearchCol");
    assertThat(field.getType()).isEqualTo("java.util.List<java.lang.String>");
  }

  @Test
  void shouldCreateSearchConditionNotEqualFieldsForSearchConditionViews() {
    given(searchConditionProvider.findFor(any()))
        .willReturn(new SearchConditionsBuilder().notIn(singletonList("test_not_equal_search_col")).build());
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
    given(searchConditionProvider.findFor(any()))
            .willReturn(new SearchConditionsBuilder().between(singletonList("test_between_search_col")).build());
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
    given(searchConditionProvider.findFor(any()))
        .willReturn(new SearchConditionsBuilder().equal(List.of("status")).build());

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

    given(searchConditionProvider.findFor(any()))
        .willReturn(new SearchConditionsBuilder().pagination(true).build());

    List<ModelScope> result = instance.create(context).stream()
        .filter(x -> x.getClassName().equals("TestTableSearchConditions"))
        .collect(toList());

    var fields = result.get(0).getFields();
    assertThat(fields).hasSize(2);

    assertThat(fields)
        .usingFieldByFieldElementComparator()
        .contains(limit, offset);
  }

  private List<String> toNamesList(List<ModelScope> modelScopes) {
    return modelScopes.stream()
        .map(ModelScope::getClassName)
        .collect(toList());
  }
}
