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

import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.TABLE_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.VIEW_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.RETENTION_READ;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.RETENTION_WRITE;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.getContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import com.epam.digital.data.platform.generator.metadata.BulkLoadInfoProvider;
import com.epam.digital.data.platform.generator.metadata.EnumProvider;
import com.epam.digital.data.platform.generator.metadata.NestedNode;
import com.epam.digital.data.platform.generator.metadata.NestedStructure;
import com.epam.digital.data.platform.generator.metadata.NestedStructureProvider;
import com.epam.digital.data.platform.generator.metadata.PartialUpdate;
import com.epam.digital.data.platform.generator.metadata.PartialUpdateProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.model.template.EnumLabel;
import com.epam.digital.data.platform.generator.utils.ContextTestUtils;
import java.util.ArrayList;
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
class RestApplicationYamlScopeFactoryTest {

  private static final List<String> CUD = List.of("create", "update", "delete");
  private static final String READ = "read";
  private static final String SEARCH = "search";

  private final Context context = getContext();

  private RestApplicationYamlScopeFactory instance;

  @Mock
  private EnumProvider enumProvider;
  @Mock
  private PartialUpdateProvider partialUpdateProvider;
  @Mock
  private NestedStructureProvider nestedStructureProvider;
  @Mock
  private BulkLoadInfoProvider bulkLoadInfoProvider;

  @BeforeEach
  void setup() {
    instance =
        new RestApplicationYamlScopeFactory(
            partialUpdateProvider, nestedStructureProvider, bulkLoadInfoProvider, enumProvider);
  }

  @Test
  void shouldCreateCorrectScopeSync() {
    var resultList = instance.create(context);

    var resultScope = resultList.get(0);
    assertThat(resultList).hasSize(1);
    assertThat(resultScope.getSearchPaths()).isEqualTo(List.of(hyphen(VIEW_NAME)));
    assertThat(resultScope.getEntityPaths())
        .containsEntry(hyphen(TABLE_NAME), List.of(hyphen(TABLE_NAME)));
    assertThat(resultScope.getRootsOfTopicNames())
        .isEqualTo(getRootsOfTopicNamesSync(TABLE_NAME));
    assertThat(resultScope.isEnumPresent()).isFalse();
    assertThat(resultScope.getRetentionPolicyDaysRead()).isEqualTo(RETENTION_READ);
    assertThat(resultScope.getRetentionPolicyDaysWrite()).isEqualTo(RETENTION_WRITE);
  }

  @Test
  void shouldCreateCorrectScopeAsync() {
    Context context = new Context(ContextTestUtils.getSettings(),
        ContextTestUtils.getCatalog(), ContextTestUtils.fullAsyncData());
    var resultList = instance.create(context);

    var resultScope = resultList.get(0);
    assertThat(resultList).hasSize(1);
    assertThat(resultScope.getSearchPaths()).isEqualTo(List.of(hyphen(VIEW_NAME)));
    assertThat(resultScope.getEntityPaths())
        .containsEntry(hyphen(TABLE_NAME), List.of(hyphen(TABLE_NAME)));
    assertThat(resultScope.getRootsOfTopicNames())
        .isEqualTo(getRootsOfTopicNamesAsync(TABLE_NAME, VIEW_NAME));
    assertThat(resultScope.isEnumPresent()).isFalse();
    assertThat(resultScope.getRetentionPolicyDaysRead()).isEqualTo(RETENTION_READ);
    assertThat(resultScope.getRetentionPolicyDaysWrite()).isEqualTo(RETENTION_WRITE);
  }

  @Test
  void shouldCreateScopeWithPartialUpdate() {
    given(partialUpdateProvider.findAll()).willReturn(List.of(
        new PartialUpdate("UPDATE_1", TABLE_NAME, Set.of("col", "my_col")),
        new PartialUpdate("UPDATE_2", TABLE_NAME, Set.of("col3"))
    ));

    var resultList = instance.create(context);

    var resultScope = resultList.get(0);
    assertThat(resultList).hasSize(1);
    assertThat(resultScope.getEntityPaths())
        .containsEntry(hyphen(TABLE_NAME),
            List.of(hyphen(TABLE_NAME), "partial/UPDATE-1", "partial/UPDATE-2"));
  }

  @Test
  void shouldCreateScopeWithNestedEntityPaths() {
    // given
    var nestedStructure = new NestedStructure();
    nestedStructure.setName("nesting_flow");
    var nestedNode = new NestedNode();
    nestedNode.setTableName(TABLE_NAME);
    nestedNode.setChildNodes(Map.of("column", new NestedNode()));
    nestedStructure.setRoot(nestedNode);

    given(nestedStructureProvider.findAll())
            .willReturn(Collections.singletonList(nestedStructure));

    // when
    var resultList = instance.create(context);

    //then
    assertThat(resultList).hasSize(1);
    var resultScope = resultList.get(0);
    assertThat(resultScope.getNestedPaths())
            .containsOnly("nested/nesting-flow");
  }

  @Test
  void shouldCreateScopeWithBulkLoad() {
    given(bulkLoadInfoProvider.getTablesWithBulkLoad()).willReturn(Set.of(TABLE_NAME));

    var resultList = instance.create(context);

    var resultScope = resultList.get(0);
    assertThat(resultList).hasSize(1);
    assertThat(resultScope.getEntityPaths())
        .containsEntry(
            hyphen(TABLE_NAME),
            List.of(
                hyphen(TABLE_NAME),
                hyphen(TABLE_NAME) + "/list",
                hyphen(TABLE_NAME) + "/csv",
                hyphen(TABLE_NAME) + "/csv/validation"));
  }

  @Test
  void shouldReturnScopeWithEnumPresent() {
    var enumLabels = Map.of("STATUS", List.of(new EnumLabel("code", "label")));
    when(enumProvider.findAllLabels()).thenReturn(enumLabels);

    var resultList = instance.create(context);

    var resultScope = resultList.get(0);
    assertThat(resultList).hasSize(1);
    assertThat(resultScope.isEnumPresent()).isTrue();
  }

  private List<String> getRootsOfTopicNamesSync(String table) {
    table = hyphen(table);

    List<String> result = new ArrayList<>();
    for (String operation : CUD) {
      result.add(operation + "-" + table);
    }

    return result;
  }

  private List<String> getRootsOfTopicNamesAsync(String table, String view) {
    table = hyphen(table);
    view = hyphen(view);

    List<String> result = new ArrayList<>();
    for (String operation : CUD) {
      result.add(operation + "-" + table);
    }
    result.add(READ + "-" + table);
    result.add(SEARCH + "-" + view);
    return result;
  }

  private String hyphen(String str) {
    return str.replace('_', '-');
  }
}
