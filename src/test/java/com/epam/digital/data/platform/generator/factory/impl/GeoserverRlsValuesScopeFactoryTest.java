/*
 * Copyright 2023 EPAM Systems.
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

import com.epam.digital.data.platform.generator.metadata.RlsMetadata;
import com.epam.digital.data.platform.generator.metadata.SearchConditionProvider;
import com.epam.digital.data.platform.generator.model.AsyncData;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.model.template.RlsFieldRestriction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;

import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.GEOMETRY_COLUMN_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.newCatalog;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withColumn;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withTable;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withTextColumn;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GeoserverRlsValuesScopeFactoryTest {

  @Mock
  private SearchConditionProvider searchConditionProvider;

  private GeoserverRlsValuesScopeFactory instance;

  @BeforeEach
  void init() {
    instance = new GeoserverRlsValuesScopeFactory(searchConditionProvider);
  }

  @Test
  void shouldReturnCorrectGeoserverRlsInfo() {
    var table1 = withTable("test_table1",
            withTextColumn("my_col"),
            withTextColumn("another_col"),
            withColumn(GEOMETRY_COLUMN_NAME, Object.class, "geometry"));
    var table2 = withTable("test_table2",
            withTextColumn("my_col"),
            withTextColumn("another_col"));
    var context = new Context(null,
            newCatalog(table1, table2),
            new AsyncData(new HashSet<>(), new HashSet<>()));
    var rls1 = new RlsMetadata(1L, "rls1", "read", "attr", "my_col", "test_table1");
    var rls2 = new RlsMetadata(2L, "rls2", "read", "attr", "my_col", "test_table2");
    when(searchConditionProvider.getRlsMetadata()).thenReturn(List.of(rls1, rls2));

    var rlsRestriction = new RlsFieldRestriction("test_table1", null, "my_col", "attr");

    var result = instance.create(context);
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getGeoRls()).hasSize(1);
    assertThat(result.get(0).getGeoRls().get(0).getRls()).usingRecursiveComparison().isEqualTo(rlsRestriction);
    assertThat(result.get(0).getGeoRls().get(0).getGeometryColumn())
        .isEqualTo(GEOMETRY_COLUMN_NAME);
  }
}
