package com.epam.digital.data.platform.generator.model.template;

import java.util.List;

public class Constraint {
  private String name;
  private List<Content> content;

  public Constraint() {}

  public Constraint(String name, List<Content> content) {
    this.name = name;
    this.content = content;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Content> getContent() {
    return content;
  }

  public void setContent(
      List<Content> content) {
    this.content = content;
  }

  public static class Content {
    private String key;
    private String value;

    public Content() {}

    public Content(String key, String value) {
      this.key = key;
      this.value = value;
    }

    public String getKey() {
      return key;
    }

    public void setKey(String key) {
      this.key = key;
    }

    public String getValue() {
      return value;
    }

    public void setValue(String value) {
      this.value = value;
    }
  }
}
