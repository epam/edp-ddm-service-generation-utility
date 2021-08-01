package com.epam.digital.data.platform.generator.model.template;

import java.util.List;
import java.util.Objects;

public class Field {

  private String type;
  private String name;
  private List<Constraint> constraints;

  public Field() {
  }

  public Field(String type, String name,
      List<Constraint> constraints) {
    this.type = type;
    this.name = name;
    this.constraints = constraints;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Constraint> getConstraints() {
    return constraints;
  }

  public void setConstraints(
      List<Constraint> constraints) {
    this.constraints = constraints;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Field field = (Field) o;
    return Objects.equals(type, field.type) && Objects.equals(name, field.name)
        && Objects.equals(constraints, field.constraints);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, name, constraints);
  }
}
