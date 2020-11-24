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

package com.epam.digital.data.platform.generator.config.properties;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "schema-crawler")
public class SchemaCrawlerProperties {

  private String schema;
  private List<String> excludeTablePrefixes;
  private List<String> excludeTables;
  private List<String> excludeFieldPrefixes;

  public String getSchema() {
    return schema;
  }

  public void setSchema(String schema) {
    this.schema = schema;
  }

  public List<String> getExcludeTablePrefixes() {
    return excludeTablePrefixes;
  }

  public void setExcludeTablePrefixes(List<String> excludeTablePrefixes) {
    this.excludeTablePrefixes = excludeTablePrefixes;
  }

  public List<String> getExcludeTables() {
    return excludeTables;
  }

  public void setExcludeTables(List<String> excludeTables) {
    this.excludeTables = excludeTables;
  }

  public List<String> getExcludeFieldPrefixes() {
    return excludeFieldPrefixes;
  }

  public void setExcludeFieldPrefixes(List<String> excludeFieldPrefixes) {
    this.excludeFieldPrefixes = excludeFieldPrefixes;
  }
}
