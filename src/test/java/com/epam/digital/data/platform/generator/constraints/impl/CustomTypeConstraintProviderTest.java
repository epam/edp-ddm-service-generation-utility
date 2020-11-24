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

import com.epam.digital.data.platform.generator.model.template.Constraint;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class CustomTypeConstraintProviderTest {

  private final CustomTypeConstraintProvider customTypeConstraintProvider =
      new CustomTypeConstraintProvider();

  @Test
  void expectConstraintIsReturnedOnCustomTypedEntity() {
    var dbType = "uuid";
    var clazzName = "com.epam.digital.data.platform.model.core.geometry.Geometry";

    var actualConstraints =
        customTypeConstraintProvider.getConstraintForProperty(dbType, clazzName);

    assertThat(actualConstraints)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactly(new Constraint("@javax.validation.Valid", Collections.emptyList()));
  }

  @Test
  void expectNoConstraintsOnDefaultType() {
    var dbType = "uuid";
    var clazzName = "java.lang.String";

    var actualConstraints =
        customTypeConstraintProvider.getConstraintForProperty(dbType, clazzName);

    assertThat(actualConstraints).isEmpty();
  }
}
