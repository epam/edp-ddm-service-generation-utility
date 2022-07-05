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
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.model.template.Constraint;
import com.epam.digital.data.platform.generator.model.template.Field;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.PK_COLUMN_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.SCHEMA_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.TABLE_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.emptyAsyncData;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.getSettings;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.newCatalog;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withColumn;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withTable;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withTextColumn;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withUuidPk;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReadNestedEntityScopeFactoryTest {

  private static final String RELATED_TABLE_NAME = "related_table";
  private static final String RELATED_SCHEMA_NAME = "RelatedTable";

  private static final String MANY_TO_MANY_COLUMN = "m2m_column";
  private static final String ONE_TO_MANY_COLUMN = "o2m_column";

  @Mock
  private EnumProvider enumProvider;
  @Mock
  private NestedReadProvider nestedReadProvider;
  @Mock
  private CompositeConstraintProvider constraintProviders;

  private Context context;

  private ReadNestedEntityScopeFactory instance;

  @BeforeEach
  void beforeEach() {
    instance =
        new ReadNestedEntityScopeFactory(enumProvider, nestedReadProvider, constraintProviders);
  }

  @Test
  void expectReadNestedEntityIsCreatedIfExistsInReadProvider() {
    context =
        new Context(
            getSettings(),
            newCatalog(
                withTable(
                    TABLE_NAME,
                    withUuidPk(PK_COLUMN_NAME),
                    withColumn(ONE_TO_MANY_COLUMN, UUID.class, "uuid"),
                    withColumn(MANY_TO_MANY_COLUMN, Object.class, "_uuid")),
                withTable(
                    RELATED_TABLE_NAME,
                    withUuidPk(PK_COLUMN_NAME),
                    withTextColumn("related_column"))),
            emptyAsyncData());
    when(nestedReadProvider.findFor(TABLE_NAME))
        .thenReturn(
            Map.of(
                ONE_TO_MANY_COLUMN,
                    new NestedReadEntity(TABLE_NAME, ONE_TO_MANY_COLUMN, RELATED_TABLE_NAME),
                MANY_TO_MANY_COLUMN,
                    new NestedReadEntity(TABLE_NAME, MANY_TO_MANY_COLUMN, RELATED_TABLE_NAME)));
    var uuidConstraints = Collections.singletonList(new Constraint());
    when(constraintProviders.getConstraintForProperty("uuid", UUID.class.getCanonicalName()))
        .thenReturn(uuidConstraints);
    when(constraintProviders.getConstraintForProperty("uuid", RELATED_SCHEMA_NAME))
            .thenReturn(Collections.emptyList());
    var nestedReadConstraints = Collections.singletonList(new Constraint());
    when(constraintProviders.getConstraintForProperty("_uuid", RELATED_SCHEMA_NAME))
        .thenReturn(nestedReadConstraints);

    var actual = instance.create(context);
    assertThat(actual).hasSize(1);
    var actualScope = actual.get(0);
    assertThat(actualScope.getClassName()).isEqualTo(SCHEMA_NAME + "ReadNested");
    assertThat(actualScope.getFields())
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactlyInAnyOrder(
            new Field(UUID.class.getCanonicalName(), "pkName", uuidConstraints),
            new Field(RELATED_SCHEMA_NAME, "o2mColumn", Collections.emptyList()),
            new Field(RELATED_SCHEMA_NAME + "[]", "m2mColumn", nestedReadConstraints));
  }
}
