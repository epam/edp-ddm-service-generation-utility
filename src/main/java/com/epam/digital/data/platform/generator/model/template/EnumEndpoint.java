package com.epam.digital.data.platform.generator.model.template;

import java.util.List;

public class EnumEndpoint {

  private String endpoint;
  private String methodName;
  private List<EnumLabel> labels;

  public EnumEndpoint(String endpoint, String methodName,
      List<EnumLabel> labels) {
    this.endpoint = endpoint;
    this.methodName = methodName;
    this.labels = labels;
  }

  public String getEndpoint() {
    return endpoint;
  }

  public void setEndpoint(String endpoint) {
    this.endpoint = endpoint;
  }

  public String getMethodName() {
    return methodName;
  }

  public void setMethodName(String methodName) {
    this.methodName = methodName;
  }

  public List<EnumLabel> getLabels() {
    return labels;
  }

  public void setLabels(
      List<EnumLabel> labels) {
    this.labels = labels;
  }
}
