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

import static org.assertj.core.api.Assertions.assertThat;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.*;

import com.epam.digital.data.platform.generator.scope.CommandHandlerScope;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AbstractCommandHandlerScopeFactoryTest {

  static class TestScope extends AbstractCommandHandlerScope {

    @Override
    protected String getOperation() {
      return "create";
    }

    @Override
    public String getPath() {
      return null;
    }
  }

  private TestScope instance;

  @BeforeEach
  void setup() {
    instance = new TestScope();
  }

  @Test
  void commandHandlerScopeFactoryTest() {
    List<CommandHandlerScope> resultList = instance.create(getContext());
    CommandHandlerScope resultScope = resultList.get(0);
    assertThat(resultList).hasSize(1);
    assertThat(resultScope.getClassName())
            .isEqualTo(SCHEMA_NAME + "CreateCommandHandler");
    assertThat(resultScope.getSchemaName())
            .isEqualTo(SCHEMA_NAME + "Model");
    assertThat(resultScope.getTableDataProviderName())
            .isEqualTo(SCHEMA_NAME + "TableDataProvider");
  }
}
