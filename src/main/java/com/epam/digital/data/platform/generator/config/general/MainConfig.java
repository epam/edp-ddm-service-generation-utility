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

package com.epam.digital.data.platform.generator.config.general;

import com.epam.digital.data.platform.generator.config.general.properties.ServiceGenerationProperties;
import com.epam.digital.data.platform.generator.model.Settings;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class MainConfig {


  @Bean
  public Settings settings(@Value("file:#{systemProperties.settings}") Resource file,
      ObjectMapper mapper) throws IOException {

    var objectReader = mapper.reader().withRootName("settings");
    try (var fis = new FileInputStream(file.getFile())) {
      return objectReader.readValue(fis, Settings.class);
    }
  }
  
  @Bean
  public ObjectMapper yamlMapper() {
    var mapper = new ObjectMapper(new YAMLFactory());
    mapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
    return mapper;
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

}
