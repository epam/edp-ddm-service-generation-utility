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

import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.getContext;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.newCatalog;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withLocalDateTimeColumn;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withTable;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withTextColumn;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withUuidPk;
import static com.epam.digital.data.platform.generator.utils.TestUtils.findByStringContains;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.epam.digital.data.platform.generator.metadata.PartialUpdate;
import com.epam.digital.data.platform.generator.metadata.PartialUpdateProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.scope.ListenerScope;
import com.epam.digital.data.platform.generator.utils.ContextTestUtils;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PartialUpdateListenerScopeFactoryTest {

  private static final String UPDATE_1 = "my_table_upd";
  private static final String UPDATE_2 = "second_test_table_upd";
  private static final String TABLE_1 = "my_table";
  private static final String TABLE_2 = "second_test_table";

  @Mock
  private PartialUpdateProvider provider;

  private PartialUpdateListenerScopeFactory instance;

  private Context context = getContext();

  @BeforeEach
  void setup() {
    instance = new PartialUpdateListenerScopeFactory(provider);

    context = new Context(null,
        newCatalog(
            withTable(TABLE_1, withUuidPk("pk"),
                withTextColumn("col"), withLocalDateTimeColumn("my_col")),
            withTable(TABLE_2, withUuidPk("pk2"),
                withTextColumn("col3"))),
        ContextTestUtils.emptyAsyncData());

  }

  @Test
  void shouldCreateListenerScope() {
    given(provider.findAll()).willReturn(List.of(
        new PartialUpdate(UPDATE_1, TABLE_1, Set.of("col", "my_col")),
        new PartialUpdate(UPDATE_2, TABLE_2, Set.of("col3"))
    ));

    var resultList = instance.create(context);

    assertThat(resultList).hasSize(2);

    var resultScope = findByStringContains("MyTable", ListenerScope::getSchemaName, resultList);
    assertThat(resultScope.getClassName()).isEqualTo("MyTableMyTableUpdListener");
    assertThat(resultScope.getSchemaName()).isEqualTo("MyTableMyTableUpd");
    assertThat(resultScope.getPkType()).isEqualTo(UUID.class.getCanonicalName());
    assertThat(resultScope.getOperation()).isEqualTo("update");
    assertThat(resultScope.getRootOfTopicName()).isEqualTo("my-table-my-table-upd");
    assertThat(resultScope.getOutputType()).isEqualTo("Void");
    assertThat(resultScope.getCommandHandler()).isEqualTo("MyTableMyTableUpdCommandHandler");
  }
}
