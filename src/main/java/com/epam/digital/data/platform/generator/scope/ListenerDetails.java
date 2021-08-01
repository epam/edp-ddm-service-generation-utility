package com.epam.digital.data.platform.generator.scope;

public class ListenerDetails {

  private final String operation;
  private final String rootOfTopicName;
  private final String inputType;
  private final String outputType;

  public ListenerDetails(String operation, String rootOfTopicName, String inputType,
      String outputType) {
    this.operation = operation;
    this.rootOfTopicName = rootOfTopicName;
    this.inputType = inputType;
    this.outputType = outputType;
  }

  public String getOperation() {
    return operation;
  }

  public String getRootOfTopicName() {
    return rootOfTopicName;
  }

  public String getInputType() {
    return inputType;
  }

  public String getOutputType() {
    return outputType;
  }
}
