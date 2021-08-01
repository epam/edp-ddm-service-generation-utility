package com.epam.digital.data.platform.generator.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class General {

  @JsonProperty("package")
  private String basePackageName;
  private String register;
  private String version;

  public String getBasePackageName() {
    return basePackageName;
  }

  public void setBasePackageName(String basePackageName) {
    this.basePackageName = basePackageName;
  }

  public String getRegister() {
    return register;
  }

  public void setRegister(String register) {
    this.register = register;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }
}
