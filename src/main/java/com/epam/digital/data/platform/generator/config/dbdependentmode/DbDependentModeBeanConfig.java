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

package com.epam.digital.data.platform.generator.config.dbdependentmode;

import com.epam.digital.data.platform.generator.metadata.AsyncDataProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.model.Settings;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import schemacrawler.schema.Catalog;

@Configuration
@Profile("!db-less-mode")
@ComponentScan("com.epam.digital.data.platform.generator")
public class DbDependentModeBeanConfig {

  @Autowired
  AsyncDataProvider asyncDataProvider;

  @Bean
  public JsonNode values(@Value("file:${PLATFORM_VALUES_PATH}") File file, ObjectMapper mapper)
      throws IOException {
    return mapper.readTree(file);
  }

  @Bean
  public Context context(Settings settings, Catalog catalog) {
    return new Context(settings, catalog, asyncDataProvider.findAll());
  }
}
