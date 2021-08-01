package com.epam.digital.data.platform.generator.scope;

import java.util.List;
import com.epam.digital.data.platform.generator.model.template.EnumEndpoint;

public class EnumControllerScope extends ClassScope {

  private List<EnumEndpoint> endpoints;

  public List<EnumEndpoint> getEndpoints() {
    return endpoints;
  }

  public void setEndpoints(
      List<EnumEndpoint> endpoints) {
    this.endpoints = endpoints;
  }
}
