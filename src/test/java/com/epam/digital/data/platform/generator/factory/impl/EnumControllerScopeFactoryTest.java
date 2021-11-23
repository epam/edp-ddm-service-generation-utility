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

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.epam.digital.data.platform.generator.metadata.EnumProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.model.template.EnumLabel;
import com.epam.digital.data.platform.generator.utils.ContextTestUtils;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import schemacrawler.schema.Catalog;

@ExtendWith(MockitoExtension.class)
class EnumControllerScopeFactoryTest {

  static final String MY_ENUM = "my-enum";
  static final String MY_ENUM_ENDPOINT = "my-enum";
  static final String MY_ENUM_METHOD_NAME = "myEnum";

  @Mock
  EnumProvider enumProvider;

  EnumControllerScopeFactory instance;

  Catalog catalog = ContextTestUtils.newCatalog();
  Context ctx = new Context(null, catalog);

  EnumLabel[] labelArr = {new EnumLabel("I", "Внесено"), new EnumLabel("D", "Видалено")};

  @BeforeEach
  void setup() {
    instance = new EnumControllerScopeFactory(enumProvider);
  }

  Map<String, List<EnumLabel>> setupLabels() {
    return Map.of(MY_ENUM, asList(labelArr));
  }

  @Test
  void shouldCreateScope() {
    // given
    given(enumProvider.findAllLabels()).willReturn(setupLabels());

    // when
    var scopes = instance.create(ctx);

    // then
    assertThat(scopes).hasSize(1);

    var endpoints = scopes.get(0).getEndpoints();
    assertThat(endpoints).hasSize(1);

    var endpoint = endpoints.get(0);
    assertThat(endpoint.getEndpoint()).isEqualTo(MY_ENUM_ENDPOINT);
    assertThat(endpoint.getMethodName()).isEqualTo(MY_ENUM_METHOD_NAME);
    assertThat(endpoint.getLabels()).containsExactly(labelArr);
  }
}
