package com.epam.digital.data.platform.generator.constraints.impl;

import com.epam.digital.data.platform.generator.constraints.ConstraintProvider;
import com.epam.digital.data.platform.generator.model.template.Constraint;
import com.epam.digital.data.platform.generator.model.template.Constraint.Content;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class MarshalingConstraintProvider implements ConstraintProvider {

  private static final Map<String, Constraint> CONSTRAINTS =
      Map.of(
          LocalDateTime.class.getCanonicalName(),
              from(
                  "com.epam.digital.data.platform.model.core.xmladapter.LocalDateTimeXmlAdapter.class"),
          LocalDate.class.getCanonicalName(),
              from(
                  "com.epam.digital.data.platform.model.core.xmladapter.LocalDateXmlAdapter.class"),
          LocalTime.class.getCanonicalName(),
              from(
                  "com.epam.digital.data.platform.model.core.xmladapter.LocalTimeXmlAdapter.class"));

  @Override
  public List<Constraint> getConstraintForProperty(String... propertyValues) {
    return Arrays.stream(propertyValues)
        .map(CONSTRAINTS::get)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  private static Constraint from(String value) {
    return new Constraint("@javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter",
            List.of(new Content("value", value)));
  }
}