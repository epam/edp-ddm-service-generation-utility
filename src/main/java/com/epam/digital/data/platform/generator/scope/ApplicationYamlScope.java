package com.epam.digital.data.platform.generator.scope;

import java.util.List;

public abstract class ApplicationYamlScope {

  private List<String> rootsOfTopicNames;

  public List<String> getRootsOfTopicNames() {
    return rootsOfTopicNames;
  }

  public void setRootsOfTopicNames(List<String> rootsOfTopicNames) {
    this.rootsOfTopicNames = rootsOfTopicNames;
  }
}
