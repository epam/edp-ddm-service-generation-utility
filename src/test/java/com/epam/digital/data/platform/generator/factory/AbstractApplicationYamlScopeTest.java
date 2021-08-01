package com.epam.digital.data.platform.generator.factory;

import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.getContext;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.override;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withTable;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withTextColumn;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.epam.digital.data.platform.generator.metadata.PartialUpdate;
import com.epam.digital.data.platform.generator.metadata.PartialUpdateProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.scope.RestApplicationYamlScope;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AbstractApplicationYamlScopeTest {

  private static final String UPDATE_1 = "my_table_upd";
  private static final String UPDATE_2 = "second_test_table_upd";
  private static final String TABLE_1 = "my_table";
  private static final String TABLE_2 = "second_test_table";

  TestApplicationYamlScope instance;

  @Mock
  PartialUpdateProvider partialUpdateProvider;

  private final Context context = getContext();

  @BeforeEach
  void setup() {
    instance = new TestApplicationYamlScope(partialUpdateProvider);
  }

  @Test
  void shouldReturnScopeWithListOfRootsOfTopicNames() {
    // given
    var crud = "test-schema";

    // when
    var scopes = instance.create(context);

    // then
    assertThat(scopes).hasSize(1);

    var scope = scopes.get(0);

    var roots = scope.getRootsOfTopicNames();
    assertThat(roots).hasSize(5);
    assertThat(roots.get(0)).isEqualTo("create-" + crud);
    assertThat(roots.get(1)).isEqualTo("read-" + crud);
    assertThat(roots.get(2)).isEqualTo("update-" + crud);
    assertThat(roots.get(3)).isEqualTo("delete-" + crud);
    assertThat(roots.get(4)).isEqualTo("search-test-schema-search");
  }

  @Test
  void shouldAddPartialUpdateTopics() {
    // given
    override(context.getCatalog(),
        withTable(TABLE_1, withTextColumn("col")),
        withTable(TABLE_2, withTextColumn("col3")));

    given(partialUpdateProvider.findAll()).willReturn(List.of(
        new PartialUpdate(UPDATE_1, TABLE_1, Set.of("col")),
        new PartialUpdate(UPDATE_2, TABLE_2, Set.of("col3"))
    ));

    // when
    var scopes = instance.create(context);

    // then
    var roots = scopes.get(0).getRootsOfTopicNames();
    assertThat(roots)
        .contains("update-my-table-my-table-upd", "update-second-test-table-second-test-table-upd");
  }

  private class TestApplicationYamlScope
      extends AbstractApplicationYamlScope<RestApplicationYamlScope> {

    private TestApplicationYamlScope(PartialUpdateProvider partialUpdateProvider) {
      super(partialUpdateProvider);
    }

    @Override
    public String getPath() {
      return EMPTY;
    }

    @Override
    protected RestApplicationYamlScope instantiate() {
      return new RestApplicationYamlScope();
    }
  }
}
