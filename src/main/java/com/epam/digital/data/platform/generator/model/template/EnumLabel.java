package com.epam.digital.data.platform.generator.model.template;

import java.util.Objects;

public class EnumLabel {

  private String code;
  private String label;

  public EnumLabel(String code, String label) {
    this.code = code;
    this.label = label;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EnumLabel enumLabel = (EnumLabel) o;
    return Objects.equals(code, enumLabel.code) && Objects
        .equals(label, enumLabel.label);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, label);
  }
}
