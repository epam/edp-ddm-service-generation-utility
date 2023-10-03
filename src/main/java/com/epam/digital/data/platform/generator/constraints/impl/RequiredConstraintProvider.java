package com.epam.digital.data.platform.generator.constraints.impl;

import com.epam.digital.data.platform.generator.constraints.ConstraintProvider;
import com.epam.digital.data.platform.generator.model.template.Constraint;
import java.util.Collections;
import java.util.List;
import schemacrawler.schema.Column;

public class RequiredConstraintProvider implements ConstraintProvider {

  private static final Constraint CONSTRAINT =
      new Constraint("@javax.validation.constraints.NotNull", Collections.emptyList());

  @Override
  public List<Constraint> getConstraintForProperty(Column column, String propertyClassName) {
    return List.of(CONSTRAINT);
  }
}
