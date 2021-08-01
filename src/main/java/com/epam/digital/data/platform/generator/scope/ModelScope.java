package com.epam.digital.data.platform.generator.scope;

import java.util.HashSet;
import java.util.Set;
import com.epam.digital.data.platform.generator.model.template.Field;

public class ModelScope extends ClassScope {

  private Set<Field> fields = new HashSet<>();

  public Set<Field> getFields() {
    return fields;
  }

  public void setFields(Set<Field> fields) {
    this.fields = fields;
  }
}
