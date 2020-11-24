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

import com.epam.digital.data.platform.generator.scope.ListenerScope;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.SCHEMA_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.getContext;
import static org.assertj.core.api.Assertions.assertThat;

class AbstractCommandListenerScopeFactoryTest {

  static class TestScope extends AbstractCommandListenerScopeFactory {

    @Override
    protected String getOperation() {
      return "update";
    }

    @Override
    protected String getOutputType() {
      return "Void";
    }

    @Override
    public String getPath() {
      return null;
    }
  }


  private static final String ROOT_OF_TOPIC_NAME = "test-schema";

  private TestScope instance;

  @BeforeEach
  void setup() {
    instance = new TestScope();
  }

  @Test
  void listenerScopeFactoryTest() {
    String expectedPkType = UUID.class.getCanonicalName();

    List<ListenerScope> resultList = instance.create(getContext());

    assertThat(resultList).hasSize(1);

    ListenerScope resultScope = resultList.get(0);
    assertThat(resultScope.getClassName()).isEqualTo(SCHEMA_NAME + "UpdateListener");
    assertThat(resultScope.getSchemaName()).isEqualTo(SCHEMA_NAME);
    assertThat(resultScope.getPkType()).isEqualTo(expectedPkType);

    assertThat(resultScope.getOperation()).isEqualTo("update");
    assertThat(resultScope.getOutputType()).isEqualTo("Void");
    assertThat(resultScope.getRootOfTopicName()).isEqualTo(ROOT_OF_TOPIC_NAME);
    assertThat(resultScope.getHandlerName()).isEqualTo(SCHEMA_NAME + "UpdateCommandHandler");
  }
}
