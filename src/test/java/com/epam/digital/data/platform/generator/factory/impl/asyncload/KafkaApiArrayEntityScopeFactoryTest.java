/*
 * Copyright 2023 EPAM Systems.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.epam.digital.data.platform.generator.factory.impl.asyncload;

import com.epam.digital.data.platform.generator.metadata.AsyncDataLoadInfoProvider;
import com.epam.digital.data.platform.generator.metadata.EnumProvider;
import com.epam.digital.data.platform.generator.metadata.NestedStructureProvider;
import com.epam.digital.data.platform.generator.model.template.Constraint;
import com.epam.digital.data.platform.generator.model.template.Field;
import com.epam.digital.data.platform.generator.scope.ModelScope;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.TABLE_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.getContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaApiArrayEntityScopeFactoryTest {

  private static final String LIMIT = "100";
  @Mock
  private AsyncDataLoadInfoProvider asyncDataLoadInfoProvider;
  @Mock
  private NestedStructureProvider nestedStructureProvider;
  @Mock
  private EnumProvider enumProvider;

  private KafkaApiArrayEntityScopeFactory instance;

  @BeforeEach
  void beforeEach() {
    instance = new KafkaApiArrayEntityScopeFactory(enumProvider, asyncDataLoadInfoProvider,
        nestedStructureProvider);
  }

  @Test
  void expectValidScopeIsCreated() {
    when(asyncDataLoadInfoProvider.getTablesWithAsyncLoad()).thenReturn(Map.of(TABLE_NAME, LIMIT));

    var actual = instance.create(getContext());

    assertThat(actual).hasSize(1);

    var expectedScope = new ModelScope();
    expectedScope.setClassName("TestSchemaModelArrayCsv");
    var expectedField = new Field();
    expectedField.setName("entities");
    expectedField.setType("TestSchemaModel[]");
    expectedField.setConstraints(
        List.of(
            new Constraint("@javax.validation.constraints.NotNull", Collections.emptyList()),
            new Constraint(
                "@javax.validation.constraints.Size",
                List.of(new Constraint.Content("max", "100"),
                    new Constraint.Content("message", "\"Maximum list size - 100\""))),
            new Constraint("@javax.validation.Valid", Collections.emptyList())));
    expectedScope.setFields(Set.of(expectedField));
    assertThat(actual.get(0)).usingRecursiveComparison().isEqualTo(expectedScope);
  }
}
