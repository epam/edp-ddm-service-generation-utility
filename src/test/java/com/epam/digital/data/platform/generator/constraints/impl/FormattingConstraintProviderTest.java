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
import java.util.List;
import org.junit.jupiter.api.Test;

class FormattingConstraintProviderTest {
  private final FormattingConstraintProvider provider = new FormattingConstraintProvider();

  @Test
  void shouldReturnConstraintForJavaClassName() {
    String dbType = "timestamp with time zone";
    String clazzName = "java.time.LocalDateTime";

    List<Constraint> constraints = provider.getConstraintForProperty(withColumn("my_col", Object.class, dbType), clazzName);

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
    String dbType = "stub";
    String clazzName = "java.lang.String";

    List<Constraint> constraints = provider.getConstraintForProperty(withColumn("my_col", Object.class, dbType), clazzName);

    assertEquals(0, constraints.size());
  }
}
