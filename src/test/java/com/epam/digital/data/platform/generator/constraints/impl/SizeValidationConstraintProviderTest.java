/*
 * Copyright 2022 EPAM Systems.
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

class SizeValidationConstraintProviderTest {

  private final SizeValidationConstraintProvider provider = new SizeValidationConstraintProvider();

  @Test
  void shouldReturnConstraintForCharDataTypeSize() {
    String dbType = "bpchar";
    String clazzName = "java.lang.String";
    Constraint expected = buildConstraint(String.valueOf(8));

    List<Constraint> constraints = provider.getConstraintForProperty(withColumn("my_col", String.class, dbType, 8), clazzName);

    assertEquals(1, constraints.size());
    assertEquals(expected.getName(), constraints.get(0).getName());
    assertEquals(1, constraints.get(0).getContent().size());
    assertEquals(expected.getContent().get(0).getKey(), constraints.get(0).getContent().get(0).getKey());
    assertEquals(String.valueOf(expected.getContent().get(0).getValue()), String.valueOf(constraints.get(0).getContent().get(0).getValue()));
  }

  private Constraint buildConstraint(String value) {
    Content content = new Content();
    content.setKey("max");
    content.setValue(value);

    Constraint constraint = new Constraint();
    constraint.setName("@javax.validation.constraints.Size");
    constraint.setContent(List.of(content));

    return constraint;
  }
}
