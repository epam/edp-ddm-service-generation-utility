/*
 * Copyright 2021 EPAM Systems.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.epam.digital.data.platform.generator.constraints.impl;

import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withColumn;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.epam.digital.data.platform.generator.model.template.Constraint;
import com.epam.digital.data.platform.generator.model.template.Constraint.Content;
import java.util.List;
import org.junit.jupiter.api.Test;

class PatternValidationConstraintProviderTest {
  private final PatternValidationConstraintProvider provider = new PatternValidationConstraintProvider();

  @Test
  void shouldReturnConstraintForDomainType() {
    String dbType = "dn_edrpou";
    String clazzName = "java.lang.String";
    Constraint expected = buildConstraint();

    List<Constraint> constraints = provider.getConstraintForProperty(withColumn("my_col", String.class, dbType), clazzName);

    assertEquals(1, constraints.size());
    assertEquals(expected.getName(), constraints.get(0).getName());
    assertEquals(1, constraints.get(0).getContent().size());
    assertEquals(expected.getContent().get(0).getKey(), constraints.get(0).getContent().get(0).getKey());
    assertEquals(expected.getContent().get(0).getValue(), constraints.get(0).getContent().get(0).getValue());
  }

  @Test
  void shouldReturnEmptyListForNotConstrainedParameter() {
    String dbType = "stub";
    String clazzName = "java.lang.String";

    List<Constraint> constraints = provider.getConstraintForProperty(withColumn("my_col", String.class, dbType), clazzName);

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
