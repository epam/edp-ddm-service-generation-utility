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

package com.epam.digital.data.platform.generator.factory;

import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.getContext;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.override;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withTable;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withTextColumn;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.epam.digital.data.platform.generator.metadata.NestedNode;
import com.epam.digital.data.platform.generator.metadata.NestedStructure;
import com.epam.digital.data.platform.generator.metadata.NestedStructureProvider;
import com.epam.digital.data.platform.generator.metadata.PartialUpdate;
import com.epam.digital.data.platform.generator.metadata.PartialUpdateProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.scope.RestApplicationYamlScope;

import com.epam.digital.data.platform.generator.utils.ContextTestUtils;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
  @Mock
  NestedStructureProvider nestedStructureProvider;

  private final Context context = getContext();

  @BeforeEach
  void setup() {
    instance = new TestApplicationYamlScope(partialUpdateProvider, nestedStructureProvider);
  }

  @Test
  void shouldReturnScopeWithListOfRootsOfTopicNamesSync() {
    // given
    var topicRoot = "test-schema";

    // when
    var scopes = instance.create(context);

    // then
    assertThat(scopes).hasSize(1);

    var scope = scopes.get(0);

    var roots = scope.getRootsOfTopicNames();
    assertThat(roots).hasSize(3);
    assertThat(roots.get(0)).isEqualTo("create-" + topicRoot);
    assertThat(roots.get(1)).isEqualTo("update-" + topicRoot);
    assertThat(roots.get(2)).isEqualTo("delete-" + topicRoot);
  }

  @Test
  void shouldReturnScopeWithListOfRootsOfTopicNamesAsync() {
    // given
    var topicRoot = "test-schema";

    // when
    Context context = new Context(ContextTestUtils.getSettings(),
        ContextTestUtils.getCatalog(), ContextTestUtils.fullAsyncData());
    var scopes = instance.create(context);

    // then
    assertThat(scopes).hasSize(1);

    var scope = scopes.get(0);

    var roots = scope.getRootsOfTopicNames();
    assertThat(roots).hasSize(5);
    assertThat(roots.get(0)).isEqualTo("create-" + topicRoot);
    assertThat(roots.get(1)).isEqualTo("update-" + topicRoot);
    assertThat(roots.get(2)).isEqualTo("delete-" + topicRoot);
    assertThat(roots.get(3)).isEqualTo("read-" + topicRoot);
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

  @Test
  void shouldAddNestedEntityTopics() {
    // given
    var nestedStructure = new NestedStructure();
    nestedStructure.setName("nesting_flow");
    var nestedNode = new NestedNode();
    nestedNode.setTableName(TABLE_1);
    nestedNode.setChildNodes(Map.of("column", new NestedNode()));
    nestedStructure.setRoot(nestedNode);

    given(nestedStructureProvider.findAll())
            .willReturn(Collections.singletonList(nestedStructure));

    // when
    var scopes = instance.create(context);

    // then
    var actualTopicRoots = scopes.get(0).getRootsOfTopicNames();
    assertThat(actualTopicRoots)
            .contains("upsert-nesting-flow-my-table-nested");
  }

  private class TestApplicationYamlScope
      extends AbstractApplicationYamlScope<RestApplicationYamlScope> {

    private TestApplicationYamlScope(PartialUpdateProvider partialUpdateProvider,
                                     NestedStructureProvider nestedStructureProvider) {
      super(partialUpdateProvider, nestedStructureProvider);
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
