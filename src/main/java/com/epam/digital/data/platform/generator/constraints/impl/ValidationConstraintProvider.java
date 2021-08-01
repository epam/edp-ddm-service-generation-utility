package com.epam.digital.data.platform.generator.constraints.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import com.epam.digital.data.platform.generator.constraints.ConstraintProvider;
import com.epam.digital.data.platform.generator.model.template.Constraint;
import com.epam.digital.data.platform.generator.model.template.Constraint.Content;

@Component
public class ValidationConstraintProvider implements ConstraintProvider {

  private static final Map<String, Constraint> CONSTRAINTS = Map.of(

    "dn_edrpou", new Constraint("@javax.validation.constraints.Pattern",
          List.of(new Content("regexp", "\"^[0-9]{8,10}$\""))),

    "dn_passport_num", new Constraint("@javax.validation.constraints.Pattern",
          List.of(new Content("regexp", "\"^[АВЕІКМНОРСТХ]{2}[0-9]{6}$\"")))
  );

  @Override
  public List<Constraint> getConstraintForProperty(String... propertyValues) {
    return Arrays.stream(propertyValues)
        .map(CONSTRAINTS::get)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }
}
