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
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.epam.digital.data.platform.generator.config.MainConfig;
import com.epam.digital.data.platform.generator.metadata.ExposeSearchConditionOption;
import com.epam.digital.data.platform.generator.metadata.SearchConditionProvider;
import com.epam.digital.data.platform.generator.model.Context;
import java.io.File;
import java.io.IOException;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RestApiValuesScopeFactoryTest {

  File file = new File("src/test/resources/registryConfigurationValues.yaml");

  @Mock
  private SearchConditionProvider searchConditionProvider;

  private final Context context = getContext();
  private final MainConfig mainConfig = new MainConfig();

  private RestApiValuesScopeFactory instance;

  @BeforeEach
  void init() throws IOException {
    instance =
        new RestApiValuesScopeFactory(
            mainConfig.values(file, mainConfig.yamlMapper()), searchConditionProvider);
  }

  @Test
  void shouldReturnCorrectValue() {
    when(searchConditionProvider.getExposedSearchConditions(ExposeSearchConditionOption.PLATFORM))
            .thenReturn(Collections.singleton("platform_search_condition"));
    when(searchConditionProvider.getExposedSearchConditions(ExposeSearchConditionOption.EXTERNAL_SYSTEM))
            .thenReturn(Collections.singleton("external_search_condition"));
    when(searchConditionProvider.getExposedSearchConditions(ExposeSearchConditionOption.PUBLIC_ACCESS))
        .thenReturn(Collections.singleton("public_search_condition"));

    var result = instance.create(context);
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getReplicationFactor()).isEqualTo(10);
    assertThat(result.get(0).getS3Signer()).isEqualTo("S3SignerType");
    assertThat(result.get(0).getExposedToPlatformPaths()).containsExactly("/platform-search-condition");
    assertThat(result.get(0).getExposedToExternalPaths()).containsExactly("/external-search-condition");
    assertThat(result.get(0).getExposedToPublicPaths()).containsExactly("/public-search-condition");
    assertThat(result.get(0).getStageName()).isEqualTo("platform");
  }
}
