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

import com.epam.digital.data.platform.generator.config.MainConfig;
import com.epam.digital.data.platform.generator.model.Context;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.getContext;
import static org.assertj.core.api.Assertions.assertThat;

class KafkaApiValuesScopeFactoryTest {

  File file = new File("src/test/resources/registryConfigurationValues.yaml");

  private final Context context = getContext();
  private final MainConfig mainConfig = new MainConfig();

  private KafkaApiValuesScopeFactory instance;

  @BeforeEach
  void init() throws IOException {
    instance = new KafkaApiValuesScopeFactory(mainConfig.values(file, mainConfig.yamlMapper()));
  }

  @Test
  void shouldReturnCorrectValue() {
    var result = instance.create(context);
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getS3Signer()).isEqualTo("S3SignerType");
  }
}
