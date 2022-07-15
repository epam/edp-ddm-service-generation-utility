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

package com.epam.digital.data.platform.generator.factory;

import com.epam.digital.data.platform.generator.constraints.impl.CompositeConstraintProvider;
import com.epam.digital.data.platform.generator.factory.impl.EntityScopeFactory;
import com.epam.digital.data.platform.generator.factory.impl.ReadNestedFieldEntityScopeFactory;
import com.epam.digital.data.platform.generator.metadata.EnumProvider;
import com.epam.digital.data.platform.generator.model.AsyncData;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.model.template.Field;
import com.epam.digital.data.platform.generator.scope.ModelScope;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.GEOMETRY_COLUMN_NAME;
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

@ExtendWith(MockitoExtension.class)
class ReadNestedFieldEntityScopeFactoryTest {

  @Mock
  private EnumProvider enumProvider;

  @Mock
  private CompositeConstraintProvider constraintProviders;

  private ReadNestedFieldEntityScopeFactory instance;

  private Context context;

  @BeforeEach
  public void init() {
    instance = new ReadNestedFieldEntityScopeFactory(enumProvider, constraintProviders);
  }

  @Test
  void shouldCreateEntityForRecentDataTables() {
    context = new Context(null,
            newCatalog(withTable("test_table",
                    withTextColumn("my_col"),
                    withTextColumn("another_col"),
                    withColumn(GEOMETRY_COLUMN_NAME, Object.class, "geometry"))),
            new AsyncData(new HashSet<>(), new HashSet<>()));
    Field expected1 = new Field(String.class.getCanonicalName(), "myCol", emptyList());
    Field expected2 = new Field(String.class.getCanonicalName(), "anotherCol", emptyList());

    var scopes = instance.create(context);

    ModelScope actualScope = scopes.get(0);
    assertThat(actualScope.getFields())
        .usingFieldByFieldElementComparator()
        .containsExactlyInAnyOrder(expected1, expected2);
  }
}
