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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BpWebserviceGatewayXmlObjectScopeFactoryTest extends AbstractBpWebserviceGatewayTest {

  private BpWebserviceGatewayXmlObjectScopeFactory factory;

  @BeforeEach
  void beforeEach() {
    var processDefinitions = getDefinitions();
    factory = new BpWebserviceGatewayXmlObjectScopeFactory(processDefinitions);
  }

  @Test
  void shouldCreateBpWebserviceGatewayXmlObjectScope() {
    var expectedScopes = factory.create(context);

    var names = expectedScopes.get(0).getClassNames();
    assertThat(names).hasSize(4);
    assertThat(names.get(0)).isEqualTo("StartFeatureCreateSchoolRequest");
    assertThat(names.get(1)).isEqualTo("StartFeatureCreateSchoolResponse");
    assertThat(names.get(2)).isEqualTo("StartBpRequest");
    assertThat(names.get(3)).isEqualTo("StartBpResponse");
  }
}
