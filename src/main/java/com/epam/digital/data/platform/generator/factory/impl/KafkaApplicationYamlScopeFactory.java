package com.epam.digital.data.platform.generator.factory.impl;

import com.epam.digital.data.platform.generator.factory.AbstractApplicationYamlScope;
import com.epam.digital.data.platform.generator.metadata.PartialUpdateProvider;
import com.epam.digital.data.platform.generator.scope.KafkaApplicationYamlScope;
import org.springframework.stereotype.Component;

@Component
public class KafkaApplicationYamlScopeFactory
    extends AbstractApplicationYamlScope<KafkaApplicationYamlScope> {

  public KafkaApplicationYamlScopeFactory(
      PartialUpdateProvider partialUpdateProvider) {
    super(partialUpdateProvider);
  }

  @Override
  public String getPath() {
    return "kafka-api/src/main/resources/application.yaml.ftl";
  }

  @Override
  protected KafkaApplicationYamlScope instantiate() {
    return new KafkaApplicationYamlScope();
  }
}
