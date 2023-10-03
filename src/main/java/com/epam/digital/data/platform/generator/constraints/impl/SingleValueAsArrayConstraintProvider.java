package com.epam.digital.data.platform.generator.constraints.impl;

import com.epam.digital.data.platform.generator.constraints.ConstraintProvider;
import com.epam.digital.data.platform.generator.model.template.Constraint;
import com.epam.digital.data.platform.generator.model.template.Constraint.Content;
import java.util.List;
import schemacrawler.schema.Column;

public class SingleValueAsArrayConstraintProvider implements ConstraintProvider {

  public static final Constraint JSON_SINGLE_VALUE_AS_ARRAY =
      new Constraint(
          "@com.fasterxml.jackson.annotation.JsonFormat",
          List.of(
              new Content("with", "com.fasterxml.jackson.annotation.JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY")
          )
      );

  @Override
  public List<Constraint> getConstraintForProperty(Column column, String propertyClassName) {
    return List.of(JSON_SINGLE_VALUE_AS_ARRAY);
  }
}
