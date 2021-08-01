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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;

import com.epam.digital.data.platform.generator.constraints.impl.CompositeConstraintProvider;
import com.epam.digital.data.platform.generator.constraints.impl.FormattingConstraintProvider;
import com.epam.digital.data.platform.generator.metadata.EnumProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.model.template.Field;
import com.epam.digital.data.platform.generator.scope.ModelScope;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EntityScopeFactoryTest {

  @Mock
  private EnumProvider enumProvider;

  @Mock
  private CompositeConstraintProvider constraintProviders;

  @Mock
  private FormattingConstraintProvider formattingConstraintProvider;

  private EntityScopeFactory instance;

  private Context context;

  @BeforeEach
  public void init() {
    instance = new EntityScopeFactory(enumProvider, constraintProviders);

    lenient().when(constraintProviders.getFormattingConstraintProvider())
        .thenReturn(formattingConstraintProvider);

    context = new Context(null,
        newCatalog(withTable("test_table"), withTable("second_test_table")));
  }

  @Test
  void shouldCreateEntityScopesForRecentDataTables() {
    override(
        context.getCatalog(),
        withTable("test_table"),
        withTable("second_test_table"));

    List<ModelScope> actual = instance.create(context);
    List<String> result = toNamesList(actual);

    assertThat(result).containsExactlyInAnyOrder("TestTable", "SecondTestTable");
  }

  @Test
  void shouldCreateFieldsForRecentDataTables() {
    Field expected1 = new Field(String.class.getCanonicalName(), "myCol", emptyList());
    Field expected2 = new Field(String.class.getCanonicalName(), "anotherCol", emptyList());
    override(
        context.getCatalog(),
        withTable("test_table",
            withTextColumn("my_col"),
            withTextColumn("another_col")));

    List<ModelScope> scopes = instance.create(context);

    ModelScope scope = scopes.get(0);
    assertThat(scope.getFields())
        .usingFieldByFieldElementComparator()
        .containsExactlyInAnyOrder(expected1, expected2);
  }

  @Test
  void shouldCreateListFieldsForRecentDataTables() {
    Field expected1 = new Field("java.util.List<java.util.UUID>", "myCol", emptyList());
    override(
        context.getCatalog(),
        withTable("test_table",
            withColumn("my_col", Object.class, "_uuid")));

    List<ModelScope> scopes = instance.create(context);

    ModelScope scope = scopes.get(0);
    assertThat(scope.getFields())
        .usingFieldByFieldElementComparator()
        .containsExactly(expected1);
  }

  @Test
  void shouldCreateEnumFieldsForRecentDataTables() {
    Field expected1 = new Field("EnStatus", "status", emptyList());
    override(
        context.getCatalog(),
        withTable("test_table",
            withColumn("status", String.class, "en_status")));
    given(enumProvider.findFor("en_status")).willReturn(Set.of("en_status"));

    List<ModelScope> scopes = instance.create(context);

    ModelScope scope = scopes.get(0);
    assertThat(scope.getFields())
        .usingFieldByFieldElementComparator()
        .containsExactly(expected1);
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
    Field expected1 = new Field(String.class.getCanonicalName(), "myCol", emptyList());
    Field expected2 = new Field(String.class.getCanonicalName(), "anotherCol", emptyList());
    override(
        context.getCatalog(),
        withSearchConditionView("test_table_search",
            withTextColumn("my_col"),
            withTextColumn("another_col")));

    List<ModelScope> scopes = instance.create(context);

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
