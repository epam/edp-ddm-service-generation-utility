package com.epam.digital.data.platform.generator.constraints.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
public class FormattingConstraintProvider implements ConstraintProvider {

  private static final Map<String, Constraint> CONSTRAINTS =
      Map.of(
          LocalDateTime.class.getCanonicalName(),
              createConstraintForPattern("\"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'\""),
          LocalDate.class.getCanonicalName(), createConstraintForPattern("\"yyyy-MM-dd\""),
          LocalTime.class.getCanonicalName(), createConstraintForPattern("\"HH:mm:ss\""));

  @Override
  public List<Constraint> getConstraintForProperty(String... propertyValues) {
    return Arrays.stream(propertyValues)
        .map(CONSTRAINTS::get)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  private static Constraint createConstraintForPattern(String pattern) {
    return new Constraint("@com.fasterxml.jackson.annotation.JsonFormat",
            List.of(new Content("shape", "com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING"),
                    new Content("pattern", pattern)));
  }
}