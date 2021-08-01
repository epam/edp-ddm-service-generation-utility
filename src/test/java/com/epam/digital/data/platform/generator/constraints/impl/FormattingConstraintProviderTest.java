package com.epam.digital.data.platform.generator.constraints.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.epam.digital.data.platform.generator.model.template.Constraint;
import java.util.List;
import org.junit.jupiter.api.Test;

class FormattingConstraintProviderTest {
  private FormattingConstraintProvider provider = new FormattingConstraintProvider();

  @Test
  void shouldReturnConstraintForJavaClassName() {
    String type = "java.time.LocalDateTime";

    List<Constraint> constraints = provider.getConstraintForProperty(type);

    assertEquals(1, constraints.size());
    assertEquals("@com.fasterxml.jackson.annotation.JsonFormat", constraints.get(0).getName());
    List<Constraint.Content> content = constraints.get(0).getContent();
    assertEquals(2, content.size());
    assertEquals("shape", content.get(0).getKey());
    assertEquals("com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING", content.get(0).getValue());
    assertEquals("pattern", content.get(1).getKey());
    assertEquals("\"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'\"", content.get(1).getValue());
  }

  @Test
  void shouldReturnEmptyListForNotConstrainedParameter() {
    String type = "stub";

    List<Constraint> constraints = provider.getConstraintForProperty(type);

    assertEquals(0, constraints.size());
  }
}
