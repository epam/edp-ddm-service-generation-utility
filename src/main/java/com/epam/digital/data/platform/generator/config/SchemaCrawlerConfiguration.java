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

package com.epam.digital.data.platform.generator.config;

import com.epam.digital.data.platform.generator.config.properties.SchemaCrawlerProperties;
import java.sql.SQLException;
import java.util.Arrays;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.inclusionrule.RegularExpressionInclusionRule;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.utility.SchemaCrawlerUtility;

@Configuration
public class SchemaCrawlerConfiguration {

  @Bean
  public LimitOptionsBuilder limitOptionsBuilder(SchemaCrawlerProperties config) {
    return LimitOptionsBuilder.builder()
        .includeSchemas(new RegularExpressionInclusionRule(config.getSchema()))
        .includeTables(includeTables(config))
        .includeColumns(includeFields(config));
  }

  private InclusionRule includeTables(SchemaCrawlerProperties config) {
    return name -> !config.getExcludeTables().contains(name)
        && config.getExcludeTablePrefixes().stream()
        .noneMatch(prfx -> name.startsWith(config.getSchema() + "." + prfx));
  }

  private InclusionRule includeFields(SchemaCrawlerProperties config) {
    return fieldName -> Arrays.stream(fieldName.split("\\."))
        .reduce((acc, cleanFieldName) -> cleanFieldName)
        .map(cleanColumnName -> config.getExcludeFieldPrefixes().stream()
            .noneMatch(cleanColumnName::startsWith))
        .orElse(true);
  }

  @Bean
  public LoadOptionsBuilder loadOptionsBuilder() {
    return LoadOptionsBuilder.builder()
        .withSchemaInfoLevel(SchemaInfoLevelBuilder.standard());
  }

  @Bean
  public SchemaCrawlerOptions schemaCrawlerOptions(LoadOptionsBuilder loadOptionsBuilder,
      LimitOptionsBuilder limitOptionsBuilder) {
    return SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
        .withLimitOptions(limitOptionsBuilder.toOptions())
        .withLoadOptions(loadOptionsBuilder.toOptions());
  }

  @Bean
  public Catalog catalog(DataSource dataSource, SchemaCrawlerOptions options)
      throws SQLException, SchemaCrawlerException {
    return SchemaCrawlerUtility.getCatalog(dataSource.getConnection(), options);
  }
}
