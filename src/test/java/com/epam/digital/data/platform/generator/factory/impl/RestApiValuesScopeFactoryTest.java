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

import com.epam.digital.data.platform.generator.config.general.MainConfig;
import com.epam.digital.data.platform.generator.config.dbdependentmode.DbDependentModeBeanConfig;
import com.epam.digital.data.platform.generator.metadata.ExposeSearchConditionOption;
import com.epam.digital.data.platform.generator.metadata.SearchConditionProvider;
import com.epam.digital.data.platform.generator.model.Context;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.FILE_COLUMN_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.PK_COLUMN_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.TABLE_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.emptyAsyncData;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.getSettings;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.newCatalog;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withColumn;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withSearchConditionView;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withTable;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withUuidPk;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestApiValuesScopeFactoryTest {

  File file = new File("src/test/resources/registryConfigurationValues.yaml");

  @Mock
  private SearchConditionProvider searchConditionProvider;

  private Context context;
  private final MainConfig mainConfig = new MainConfig();
  private final DbDependentModeBeanConfig dbDependentModeBeanConfig = new DbDependentModeBeanConfig();

  private RestApiValuesScopeFactory instance;

  @BeforeEach
  void init() throws IOException {
    context = new Context(
            getSettings(),
            newCatalog(
                    withTable(TABLE_NAME, withUuidPk(PK_COLUMN_NAME),
                            withColumn(FILE_COLUMN_NAME, Object.class, "type_file")),
                    withSearchConditionView(
                            "file_search_condition",
                            withColumn(FILE_COLUMN_NAME, Object.class, "type_file")),
                    withSearchConditionView("platform_search_condition"),
                    withSearchConditionView("external_search_condition"),
                    withSearchConditionView("public_search_condition")),
            emptyAsyncData());

    instance =
        new RestApiValuesScopeFactory(
            dbDependentModeBeanConfig.values(file, mainConfig.yamlMapper()), searchConditionProvider);
  }

  @Test
  void shouldReturnCorrectValue() {
    when(searchConditionProvider.getAllExposedSearchConditions()).thenReturn(Set.of("platform_search_condition",
            "external_search_condition", "public_search_condition", "file_search_condition"));
    when(searchConditionProvider.getExposedSearchConditionsByType(ExposeSearchConditionOption.PLATFORM))
            .thenReturn(Collections.singleton("platform_search_condition"));
    when(searchConditionProvider.getExposedSearchConditionsByType(ExposeSearchConditionOption.EXTERNAL_SYSTEM))
            .thenReturn(Collections.singleton("external_search_condition"));
    when(searchConditionProvider.getExposedSearchConditionsByType(
            ExposeSearchConditionOption.PUBLIC_ACCESS))
        .thenReturn(Set.of("public_search_condition", "file_search_condition"));

    var result = instance.create(context);
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getReplicationFactor()).isEqualTo(10);
    assertThat(result.get(0).getS3Signer()).isEqualTo("S3SignerType");
    assertThat(result.get(0).getExposedToPlatformInfo().getPaths()).containsExactly("/platform-search-condition");
    assertThat(result.get(0).getExposedToPlatformInfo().isAnyResponseContainsFile()).isFalse();
    assertThat(result.get(0).getExposedToExternalInfo().getPaths()).containsExactly("/external-search-condition");
    assertThat(result.get(0).getExposedToExternalInfo().isAnyResponseContainsFile()).isFalse();
    assertThat(result.get(0).getExposedToPublicInfo().getPaths()).containsExactly("/public-search-condition", "/file-search-condition");
    assertThat(result.get(0).getExposedToPublicInfo().isAnyResponseContainsFile()).isTrue();
    assertThat(result.get(0).getStageName()).isEqualTo("platform");
  }
}
