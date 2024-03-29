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

import com.epam.digital.data.platform.generator.model.template.Constraint;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withColumn;
import static org.assertj.core.api.Assertions.assertThat;

class NestedConstraintProviderTest {

  private final NestedConstraintProvider nestedConstraintProvider = new NestedConstraintProvider();

  @Test
  void expectConstraintIsReturnedOnNestedEntity() {
    var dbType = "uuid";
    var clazzName = "NestedEntity";

    var actualConstraints = nestedConstraintProvider.getConstraintForProperty(withColumn("my_col", UUID.class, dbType), clazzName);

    assertThat(actualConstraints)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactly(new Constraint("@javax.validation.Valid", Collections.emptyList()));
  }

  @Test
  void expectNoConstraintsOnNonNestedType() {
    var dbType = "uuid";
    var clazzName = "java.lang.UUID";

    var actualConstraints = nestedConstraintProvider.getConstraintForProperty(withColumn("my_col", UUID.class, dbType), clazzName);

    assertThat(actualConstraints).isEmpty();
  }
}
