package com.epam.digital.data.platform.generator.constraints.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.epam.digital.data.platform.generator.model.template.Constraint;
import com.epam.digital.data.platform.generator.model.template.Constraint.Content;
import java.util.List;
import org.junit.jupiter.api.Test;

class ValidationConstraintProviderTest {
  private ValidationConstraintProvider provider = new ValidationConstraintProvider();

  @Test
  void shouldReturnConstraintForDomainType() {
    String type = "dn_edrpou";
    Constraint expected = buildConstraint();

    List<Constraint> constraints = provider.getConstraintForProperty(type);

    assertEquals(1, constraints.size());
    assertEquals(expected.getName(), constraints.get(0).getName());
    assertEquals(1, constraints.get(0).getContent().size());
    assertEquals(expected.getContent().get(0).getKey(), constraints.get(0).getContent().get(0).getKey());
    assertEquals(expected.getContent().get(0).getValue(), constraints.get(0).getContent().get(0).getValue());
  }

  @Test
  void shouldReturnEmptyListForNotConstrainedParameter() {
    String type = "stub";

    List<Constraint> constraints = provider.getConstraintForProperty(type);

    assertEquals(0, constraints.size());
  }

  private Constraint buildConstraint() {
    Content content = new Content();
    content.setKey("regexp");
    content.setValue("\"^[0-9]{8,10}$\"");

    Constraint constraint = new Constraint();
    constraint.setName("@javax.validation.constraints.Pattern");
    constraint.setContent(List.of(content));

    return constraint;
  }
}
