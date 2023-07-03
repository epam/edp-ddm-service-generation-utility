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
import com.epam.digital.data.platform.generator.model.template.Constraint.Content;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;
import schemacrawler.schema.Column;

@Component
public class SizeValidationConstraintProvider implements ConstraintProvider {

  private static final String SIZE_CONSTRAINT = "@javax.validation.constraints.Size";
  private static final Set<String> SIZE_CONSTRAINT_DATA_TYPES = Set.of("bpchar", "varchar");

  @Override
  public List<Constraint> getConstraintForProperty(Column column,
      String propertyClassName) {
    if (SIZE_CONSTRAINT_DATA_TYPES.contains(column.getColumnDataType().getName())) {
      return List.of(
          new Constraint(
              SIZE_CONSTRAINT, List.of(new Content("max", Integer.toString(column.getSize())))));
    }
    return Collections.emptyList();
  }
}
