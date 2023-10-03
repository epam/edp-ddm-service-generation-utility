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

package com.epam.digital.data.platform.generator.factory.impl.bpwebservicegateway;

import static com.epam.digital.data.platform.generator.factory.impl.bpwebservicegateway.BpWebserviceGatewaySoapDtoScopeFactory.ATTRIBUTE_REQUIRED;
import static com.epam.digital.data.platform.generator.factory.impl.bpwebservicegateway.BpWebserviceGatewaySoapDtoScopeFactory.BUSINESS_PROCESS_DEFINITION_KEY_FIELD;
import static com.epam.digital.data.platform.generator.factory.impl.bpwebservicegateway.BpWebserviceGatewaySoapDtoScopeFactory.MAP_STRING_OBJECT_TYPE;
import static com.epam.digital.data.platform.generator.factory.impl.bpwebservicegateway.BpWebserviceGatewaySoapDtoScopeFactory.MAP_STRING_STRING_TYPE;
import static com.epam.digital.data.platform.generator.factory.impl.bpwebservicegateway.BpWebserviceGatewaySoapDtoScopeFactory.RESULT_VARIABLES_FIELD;
import static com.epam.digital.data.platform.generator.factory.impl.bpwebservicegateway.BpWebserviceGatewaySoapDtoScopeFactory.START_VARIABLES_FIELD;
import static com.epam.digital.data.platform.generator.factory.impl.bpwebservicegateway.BpWebserviceGatewaySoapDtoScopeFactory.STRING_TYPE;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BpWebserviceGatewaySoapDtoScopeFactoryTest extends AbstractBpWebserviceGatewayTest {

  private BpWebserviceGatewaySoapDtoScopeFactory factory;

  @BeforeEach
  void beforeEach() {
    var processDefinitions = getDefinitions();
    factory = new BpWebserviceGatewaySoapDtoScopeFactory(processDefinitions);
  }

  @Test
  void shouldCreateBpWebserviceGatewayDtoScope() {
    var expectedScopes = factory.create(context);

    assertThat(expectedScopes).hasSize(4);
    assertThat(expectedScopes.get(0).getClassName()).isEqualTo("StartFeatureCreateSchoolRequest");
    assertThat(expectedScopes.get(0).getFields()).hasSize(1);
    assertThat(expectedScopes.get(0).getFields().get(0).getType()).isEqualTo(
        MAP_STRING_STRING_TYPE);
    assertThat(expectedScopes.get(0).getFields().get(0).getName()).isEqualTo(
        START_VARIABLES_FIELD);
    assertThat(expectedScopes.get(0).getFields().get(0).getConstraints().get(0).getContent().get(0)
        .getValue()).isEqualTo(Boolean.FALSE.toString());
    assertThat(expectedScopes.get(0).getFields().get(0).getConstraints().get(0).getContent().get(0)
        .getKey()).isEqualTo(ATTRIBUTE_REQUIRED);

    assertThat(expectedScopes.get(1).getClassName()).isEqualTo("StartBpRequest");
    assertThat(expectedScopes.get(1).getFields()).hasSize(2);
    assertThat(expectedScopes.get(1).getFields().get(0).getType()).isEqualTo(STRING_TYPE);
    assertThat(expectedScopes.get(1).getFields().get(0).getName()).isEqualTo(
        BUSINESS_PROCESS_DEFINITION_KEY_FIELD);
    assertThat(expectedScopes.get(1).getFields().get(1).getType()).isEqualTo(
        MAP_STRING_STRING_TYPE);
    assertThat(expectedScopes.get(1).getFields().get(1).getName()).isEqualTo(
        START_VARIABLES_FIELD);
    assertThat(expectedScopes.get(1).getFields().get(1).getConstraints().get(0).getContent().get(0)
        .getValue()).isEqualTo(Boolean.FALSE.toString());
    assertThat(expectedScopes.get(1).getFields().get(1).getConstraints().get(0).getContent().get(0)
        .getKey()).isEqualTo(ATTRIBUTE_REQUIRED);

    assertThat(expectedScopes.get(2).getClassName()).isEqualTo("StartFeatureCreateSchoolResponse");
    assertThat(expectedScopes.get(2).getFields()).hasSize(1);
    assertThat(expectedScopes.get(2).getFields().get(0).getType()).isEqualTo(
        MAP_STRING_OBJECT_TYPE);
    assertThat(expectedScopes.get(2).getFields().get(0).getName()).isEqualTo(
        RESULT_VARIABLES_FIELD);
    assertThat(expectedScopes.get(2).getFields().get(0).getConstraints().get(0).getContent().get(0)
        .getValue()).isEqualTo(Boolean.TRUE.toString());
    assertThat(expectedScopes.get(2).getFields().get(0).getConstraints().get(0).getContent().get(0)
        .getKey()).isEqualTo(ATTRIBUTE_REQUIRED);

    assertThat(expectedScopes.get(3).getClassName()).isEqualTo("StartBpResponse");
    assertThat(expectedScopes.get(3).getFields()).hasSize(1);
    assertThat(expectedScopes.get(3).getFields().get(0).getType()).isEqualTo(
        MAP_STRING_OBJECT_TYPE);
    assertThat(expectedScopes.get(3).getFields().get(0).getName()).isEqualTo(
        RESULT_VARIABLES_FIELD);
    assertThat(expectedScopes.get(3).getFields().get(0).getConstraints().get(0).getContent().get(0)
        .getValue()).isEqualTo(Boolean.TRUE.toString());
    assertThat(expectedScopes.get(3).getFields().get(0).getConstraints().get(0).getContent().get(0)
        .getKey()).isEqualTo(ATTRIBUTE_REQUIRED);
  }
}
