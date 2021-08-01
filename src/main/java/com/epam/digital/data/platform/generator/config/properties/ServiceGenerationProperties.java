package com.epam.digital.data.platform.generator.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")
public class ServiceGenerationProperties {

  private String templates;

  public String getTemplates() {
    return templates;
  }

  public void setTemplates(String templates) {
    this.templates = templates;
  }

}
