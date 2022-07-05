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

import com.epam.digital.data.platform.generator.metadata.NestedReadEntity;
import com.epam.digital.data.platform.generator.metadata.NestedReadProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.permissionmap.PermissionMap;
import com.epam.digital.data.platform.generator.scope.ReadControllerScope;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.COLUMN_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.ENDPOINT;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.PK_COLUMN_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.PK_NAME;
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
class ReadControllerScopeFactoryTest {

  private static final Set<String> ROLE_SET = Set.of("hasRole('role1')", "hasRole('role2')");
  private static final String RELATED_TABLE_NAME = "related_table";
  private static final String RELATED_SCHEMA_NAME = "RelatedTable";

  @Mock
  private PermissionMap permissionMap;
  @Mock
  private NestedReadProvider nestedReadProvider;

  private ReadControllerScopeFactory instance;

  private Context context;

  @BeforeEach
  void setup() {
    context =
        new Context(
            getSettings(),
            newCatalog(
                withTable(TABLE_NAME, withUuidPk(PK_COLUMN_NAME),
                        withColumn(COLUMN_NAME, UUID.class, "uuid")),
                withTable(RELATED_TABLE_NAME, withUuidPk(PK_COLUMN_NAME),
                        withTextColumn("related_column"))),
            emptyAsyncData());
    instance = new ReadControllerScopeFactory(permissionMap, nestedReadProvider);
  }

  @Test
  void shouldCreateControllerScope() {
    when(permissionMap.getReadExpressionsFor(TABLE_NAME)).thenReturn(ROLE_SET);
    when(permissionMap.getReadExpressionsFor(RELATED_TABLE_NAME))
        .thenReturn(Collections.emptySet());

    List<ReadControllerScope> resultList = instance.create(context);

    assertThat(resultList).hasSize(2);

    ReadControllerScope resultScope1 = resultList.get(0);
    assertThat(resultScope1.getClassName()).isEqualTo(SCHEMA_NAME + "ReadController");
    assertThat(resultScope1.getSchemaName()).isEqualTo(SCHEMA_NAME);
    assertThat(resultScope1.getPkName()).isEqualTo(PK_NAME);
    assertThat(resultScope1.getEndpoint()).isEqualTo(ENDPOINT);
    assertThat(resultScope1.getPkType()).isEqualTo(UUID.class.getCanonicalName());
    assertThat(resultScope1.getReadRoles()).containsExactlyElementsOf(ROLE_SET);
    assertThat(resultScope1.getServiceName()).isEqualTo(SCHEMA_NAME + "ReadService");

    ReadControllerScope resultScope2 = resultList.get(1);
    assertThat(resultScope2.getClassName()).isEqualTo(RELATED_SCHEMA_NAME + "ReadController");
    assertThat(resultScope2.getSchemaName()).isEqualTo(RELATED_SCHEMA_NAME);
    assertThat(resultScope2.getPkName()).isEqualTo(PK_NAME);
    assertThat(resultScope2.getEndpoint()).isEqualTo("/related-table");
    assertThat(resultScope2.getPkType()).isEqualTo(UUID.class.getCanonicalName());
    assertThat(resultScope2.getReadRoles()).isEmpty();
    assertThat(resultScope2.getServiceName()).isEqualTo(RELATED_SCHEMA_NAME + "ReadService");
  }


  @Test
  void shouldCreateControllerScopeWithNestedReads() {
    when(permissionMap.getReadExpressionsFor(Set.of(TABLE_NAME, RELATED_TABLE_NAME))).thenReturn(ROLE_SET);
    when(permissionMap.getReadExpressionsFor(RELATED_TABLE_NAME))
            .thenReturn(Collections.emptySet());
    when(nestedReadProvider.findFor(TABLE_NAME)).thenReturn(
            Map.of(COLUMN_NAME, new NestedReadEntity(TABLE_NAME, COLUMN_NAME, RELATED_TABLE_NAME)));

    List<ReadControllerScope> resultList = instance.create(context);

    assertThat(resultList).hasSize(2);

    ReadControllerScope resultScope1 = resultList.get(0);
    assertThat(resultScope1.getClassName()).isEqualTo(SCHEMA_NAME + "ReadController");
    assertThat(resultScope1.getSchemaName()).isEqualTo(SCHEMA_NAME + "ReadNested");
    assertThat(resultScope1.getPkName()).isEqualTo(PK_NAME);
    assertThat(resultScope1.getEndpoint()).isEqualTo(ENDPOINT);
    assertThat(resultScope1.getPkType()).isEqualTo(UUID.class.getCanonicalName());
    assertThat(resultScope1.getReadRoles()).containsExactlyElementsOf(ROLE_SET);
    assertThat(resultScope1.getServiceName()).isEqualTo(SCHEMA_NAME + "ReadService");

    ReadControllerScope resultScope2 = resultList.get(1);
    assertThat(resultScope2.getClassName()).isEqualTo(RELATED_SCHEMA_NAME + "ReadController");
    assertThat(resultScope2.getSchemaName()).isEqualTo(RELATED_SCHEMA_NAME);
    assertThat(resultScope2.getPkName()).isEqualTo(PK_NAME);
    assertThat(resultScope2.getEndpoint()).isEqualTo("/related-table");
    assertThat(resultScope2.getPkType()).isEqualTo(UUID.class.getCanonicalName());
    assertThat(resultScope2.getReadRoles()).isEmpty();
    assertThat(resultScope2.getServiceName()).isEqualTo(RELATED_SCHEMA_NAME + "ReadService");
  }
}
