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

import com.epam.digital.data.platform.generator.config.properties.ServiceGenerationProperties;
import com.epam.digital.data.platform.generator.metadata.AsyncDataProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.model.Settings;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import schemacrawler.schema.Catalog;

@Configuration
public class MainConfig {

  @Autowired
  private AsyncDataProvider asyncDataProvider;

  @Bean
  public Settings settings(@Value("file:#{systemProperties.settings}") Resource file,
      ObjectMapper mapper) throws IOException {

    var objectReader = mapper.reader().withRootName("settings");
    try (var fis = new FileInputStream(file.getFile())) {
      return objectReader.readValue(fis, Settings.class);
    }
  }
  
  @Bean
  public JsonNode values(@Value("file:${PLATFORM_VALUES_PATH}") File file, ObjectMapper mapper)
      throws IOException {
    return mapper.readTree(file);
  }

  @Bean
  public ObjectMapper yamlMapper() {
    return new ObjectMapper(new YAMLFactory());
  }

  @Bean
  @Qualifier("rootTemplates")
  public Path getRootTemplatePath(ServiceGenerationProperties properties)
      throws IOException, URISyntaxException {
    URL uri = this.getClass().getClassLoader().getResource(properties.getTemplates());

    Path root;
    if ("jar".equals(uri.toURI().getScheme())) {
      Map<String, String> env = new HashMap<>();
      String[] array = uri.toString().split("!");
      FileSystem fs = FileSystems.newFileSystem(URI.create(array[0]), env);
      root = fs.getPath(properties.getTemplates());
    } else {
      root = Paths.get(uri.toURI());
    }
    return root;
  }

  @Bean
  public Context context(Settings settings, Catalog catalog) {
    return new Context(settings, catalog, asyncDataProvider.findAll());
  }
}
