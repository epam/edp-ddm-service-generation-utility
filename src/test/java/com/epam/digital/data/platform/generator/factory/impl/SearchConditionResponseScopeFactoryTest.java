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

import com.epam.digital.data.platform.generator.constraints.impl.CompositeConstraintProvider;
import com.epam.digital.data.platform.generator.metadata.EnumProvider;
import com.epam.digital.data.platform.generator.metadata.NestedReadEntity;
import com.epam.digital.data.platform.generator.metadata.NestedReadProvider;
import com.epam.digital.data.platform.generator.metadata.SearchConditionProvider;
import com.epam.digital.data.platform.generator.metadata.SearchConditionsBuilder;
import com.epam.digital.data.platform.generator.model.AsyncData;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.model.template.Constraint;
import com.epam.digital.data.platform.generator.model.template.Field;
import com.epam.digital.data.platform.generator.scope.ModelScope;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.newCatalog;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.override;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withColumn;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withSearchConditionView;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withTable;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withTextColumn;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchConditionResponseScopeFactoryTest {

  @Mock
  private EnumProvider enumProvider;
  @Mock
  private SearchConditionProvider searchConditionProvider;
  @Mock
  private NestedReadProvider nestedReadProvider;
  @Mock
  private CompositeConstraintProvider constraintProviders;

  private Context context;

  private SearchConditionResponseScopeFactory instance;

  @BeforeEach
  void beforeEach() {
    instance =
        new SearchConditionResponseScopeFactory(
            enumProvider, searchConditionProvider, nestedReadProvider, constraintProviders);

    context =
        new Context(
            null,
            newCatalog(withTable("test_table"), withTable("second_test_table")),
            new AsyncData(new HashSet<>(), new HashSet<>()));
  }

  @Test
  void shouldCreateEntityScopesForSearchConditionViews() {
    override(
            context.getCatalog(),
            withSearchConditionView("test_table_search"),
            withSearchConditionView("second_test_table_search"));

    List<String> result = toNamesList(instance.create(context));

    assertThat(result).contains("TestTableSearch", "SecondTestTableSearch");
  }

  @Test
  void shouldCreateFieldsForSearchConditionViews() {
    override(
        context.getCatalog(),
        withSearchConditionView(
            "test_table_search",
            withTextColumn("my_col"),
            withTextColumn("another_col"),
            withTextColumn("not_returning_column")));
    given(searchConditionProvider.findFor("test_table_search"))
        .willReturn(
            new SearchConditionsBuilder()
                .returningColumns(List.of("my_col", "another_col"))
                .build());


    List<ModelScope> scopes = instance.create(context);

    Field expected1 = new Field(String.class.getCanonicalName(), "myCol", emptyList());
    Field expected2 = new Field(String.class.getCanonicalName(), "anotherCol", emptyList());

    ModelScope scope = scopes.get(0);
    assertThat(scope.getFields())
        .usingFieldByFieldElementComparator()
        .containsExactlyInAnyOrder(expected1, expected2);
  }

  @Test
  void shouldCreateEnumFields() {
    override(
            context.getCatalog(),
            withSearchConditionView("test_table_search",
                    withColumn("status", String.class, "en_status")));
    given(enumProvider.findFor("en_status")).willReturn(Set.of("en_status"));
    given(searchConditionProvider.findFor("test_table_search"))
            .willReturn(
                    new SearchConditionsBuilder()
                            .returningColumns(List.of("status"))
                            .build());

    List<ModelScope> scopes = instance.create(context);

    Field expected = new Field("EnStatus", "status", emptyList());
    ModelScope scope = scopes.get(0);
    assertThat(scope.getFields())
            .usingFieldByFieldElementComparator()
            .containsExactly(expected);
  }

  @Test
  void shouldCreateFieldsForNestedRead() {
    var myColumn = withColumn("my_col", Object.class, "_uuid");
    var anotherColumn = withColumn("another_col", Object.class, "uuid");
    override(
            context.getCatalog(),
            withSearchConditionView(
                    "test_table_search",
                    myColumn,
                    anotherColumn,
                    withTextColumn("not_returning_column")),
            withTable("related_table"));
    given(searchConditionProvider.findFor("test_table_search"))
            .willReturn(
                    new SearchConditionsBuilder()
                            .returningColumns(List.of("my_col", "another_col"))
                            .build());
    NestedReadEntity m2mNestedReadEntity =
        new NestedReadEntity("test_table_search", "my_col", "related_table");
    NestedReadEntity o2mNestedReadEntity = new NestedReadEntity("test_table_search", "another_col",
            "related_table");
    given(nestedReadProvider.findFor("test_table_search"))
            .willReturn(Map.of("my_col", m2mNestedReadEntity,
                    "another_col", o2mNestedReadEntity));
    given(constraintProviders.getConstraintForProperty(anotherColumn, "RelatedTableReadNested"))
            .willReturn(emptyList());
    var nestedReadConstraints = Collections.singletonList(new Constraint());
    when(constraintProviders.getConstraintForProperty(myColumn, "RelatedTableReadNested"))
            .thenReturn(nestedReadConstraints);

    List<ModelScope> scopes = instance.create(context);

    Field expected1 = new Field("RelatedTableReadNested[]", "myCol", nestedReadConstraints);
    Field expected2 = new Field("RelatedTableReadNested", "anotherCol", emptyList());

    ModelScope scope = scopes.get(0);
    assertThat(scope.getFields())
            .usingFieldByFieldElementComparator()
            .containsExactlyInAnyOrder(expected1, expected2);
  }

  private List<String> toNamesList(List<ModelScope> modelScopes) {
    return modelScopes.stream()
            .map(ModelScope::getClassName)
            .collect(toList());
  }
}
