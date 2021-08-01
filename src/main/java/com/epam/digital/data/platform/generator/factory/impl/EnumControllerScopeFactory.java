package com.epam.digital.data.platform.generator.factory.impl;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

import com.epam.digital.data.platform.generator.metadata.EnumProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.model.template.EnumEndpoint;
import com.epam.digital.data.platform.generator.model.template.EnumLabel;
import com.epam.digital.data.platform.generator.scope.EnumControllerScope;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.springframework.stereotype.Component;
import com.epam.digital.data.platform.generator.factory.AbstractScope;

@Component
public class EnumControllerScopeFactory extends AbstractScope<EnumControllerScope> {

  private final EnumProvider enumProvider;

  public EnumControllerScopeFactory(
      EnumProvider enumProvider) {
    this.enumProvider = enumProvider;
  }

  @Override
  public List<EnumControllerScope> create(Context context) {
    Map<String, List<EnumLabel>> labels = enumProvider.findAllLabels();

    List<EnumEndpoint> endpoints = labels.entrySet().stream()
        .map(this::toEnumEndpoint)
        .collect(toList());

    var scope = new EnumControllerScope();
    scope.setClassName("EnumController");
    scope.setEndpoints(endpoints);
    return singletonList(scope);
  }

  private EnumEndpoint toEnumEndpoint(Entry<String, List<EnumLabel>> e) {
    return new EnumEndpoint(
        toHyphenTableName(e.getKey()),
        getPropertyName(e.getKey()),
        e.getValue());
  }

  @Override
  public String getPath() {
    return "rest-api/src/main/java/restapi/controller/enumController.java.ftl";
  }
}
