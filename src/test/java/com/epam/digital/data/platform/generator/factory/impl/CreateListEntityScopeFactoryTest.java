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

import com.epam.digital.data.platform.generator.metadata.BulkLoadInfoProvider;
import com.epam.digital.data.platform.generator.metadata.EnumProvider;
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
import java.util.Set;

import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.TABLE_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.getContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateListEntityScopeFactoryTest {

  @Mock
  private BulkLoadInfoProvider bulkLoadInfoProvider;
  @Mock
  private EnumProvider enumProvider;

  private CreateListEntityScopeFactory instance;

  @BeforeEach
  void beforeEach() {
    instance = new CreateListEntityScopeFactory(enumProvider, bulkLoadInfoProvider);
  }

  @Test
  void expectValidScopeIsCreated() {
    when(bulkLoadInfoProvider.getTablesWithBulkLoad()).thenReturn(Set.of(TABLE_NAME));

    var actual = instance.create(getContext());

    assertThat(actual).hasSize(1);

    var expectedScope = new ModelScope();
    expectedScope.setClassName("TestSchemaCreateList");
    var expectedField = new Field();
    expectedField.setName("entities");
    expectedField.setType("TestSchemaModel[]");
    expectedField.setConstraints(
        List.of(
            new Constraint("@javax.validation.constraints.NotNull", Collections.emptyList()),
            new Constraint(
                "@javax.validation.constraints.Size",
                Collections.singletonList(new Constraint.Content("max", 50))),
            new Constraint("@javax.validation.Valid", Collections.emptyList())));
    expectedScope.setFields(Set.of(expectedField));
    assertThat(actual.get(0)).usingRecursiveComparison().isEqualTo(expectedScope);
  }
}
