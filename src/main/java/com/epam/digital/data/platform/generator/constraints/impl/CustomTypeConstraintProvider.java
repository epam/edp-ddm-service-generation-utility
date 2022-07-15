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

import com.epam.digital.data.platform.generator.constraints.ConstraintProvider;
import com.epam.digital.data.platform.generator.model.template.Constraint;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class CustomTypeConstraintProvider implements ConstraintProvider {

  private static final String CUSTOM_TYPE_PREFIX = "com.epam.digital.data.platform.model.core";

  private static final Constraint VALID_CONSTRAINT =
      new Constraint("@javax.validation.Valid", Collections.emptyList());

  @Override
  public List<Constraint> getConstraintForProperty(
      String propertyDataType, String propertyClassName) {
    var isPropertyOfCustomType = propertyClassName.startsWith(CUSTOM_TYPE_PREFIX);
    if (isPropertyOfCustomType) {
      return Collections.singletonList(VALID_CONSTRAINT);
    } else {
      return Collections.emptyList();
    }
  }
}
