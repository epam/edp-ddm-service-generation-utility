package com.epam.digital.data.platform.generator.config;

import freemarker.cache.TemplateLoader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class SimpleClasspathLoader implements TemplateLoader {

  @Override
  public Reader getReader(Object name, String encoding) throws IOException {
    URL url = getClass().getClassLoader().getResource(String.valueOf(name));
    if (url == null) {
      throw new IllegalStateException(name + " not found on classpath");
    }
    URLConnection connection = url.openConnection();
    connection.setUseCaches(false);

    InputStream is = connection.getInputStream();

    return new InputStreamReader(is, StandardCharsets.UTF_8);
  }

  @Override
  public long getLastModified(Object templateSource) {
    return 0;
  }

  @Override
  public Object findTemplateSource(String name) {
    return "META-INF/templates/" + name;
  }

  @Override
  public void closeTemplateSource(Object templateSource) {
    // NOT APPLICABLE
  }

}
