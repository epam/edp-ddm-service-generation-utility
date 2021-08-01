package com.epam.digital.data.platform.generator.model.template;

public class SelectableField {

  private String name;
  private String converter;

  public SelectableField(String name, String converter) {
    this.name = name;
    this.converter = converter;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getConverter() {
    return converter;
  }

  public void setConverter(String converter) {
    this.converter = converter;
  }
}
