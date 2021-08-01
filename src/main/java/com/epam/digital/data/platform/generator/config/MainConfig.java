package com.epam.digital.data.platform.generator.config;

import com.epam.digital.data.platform.generator.config.properties.ServiceGenerationProperties;
import com.epam.digital.data.platform.generator.model.Blueprint;
import com.epam.digital.data.platform.generator.model.Context;
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
import schemacrawler.schema.Catalog;

@Configuration
public class MainConfig {

  @Bean
  public Blueprint blueprint(@Value("file:#{systemProperties.settings}") Resource file,
      ObjectMapper mapper)
      throws IOException {
    return mapper.readValue(new FileInputStream(file.getFile()), Blueprint.class);
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
  public Context context(Blueprint blueprint, Catalog catalog) {
    return new Context(blueprint, catalog);
  }
}
