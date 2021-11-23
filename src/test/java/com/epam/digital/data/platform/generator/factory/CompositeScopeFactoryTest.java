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

import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.utils.PathConverter;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CompositeScopeFactoryTest {

  private static final String CORRECT_PATH = PathConverter.safePath("ua/gov/Main.class");

  private TestScopeFactory testScopeFactory;
  private DefaultScopeFactory defaultScopeFactory;

  private class TestScopeFactory implements ScopeFactory {

    @Override
    public List create(Context context) {
      return Arrays.asList("A");
    }

    @Override
    public String getPath() {
      return CORRECT_PATH;
    }
  }

  @BeforeEach
  void init() {
    testScopeFactory = new TestScopeFactory();
    defaultScopeFactory = new DefaultScopeFactory(List.of(testScopeFactory));
  }

  @Test
  void shouldReturnResultAsCompositeOfResults() {
    defaultScopeFactory = new DefaultScopeFactory(List.of(testScopeFactory, testScopeFactory));

    var resultScope = defaultScopeFactory.create(CORRECT_PATH);
    var resultList = resultScope.create(null);

    assertThat(resultList)
        .hasSize(2)
        .containsOnly("A");
  }
}