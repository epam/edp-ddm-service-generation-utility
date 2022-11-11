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

import static java.util.stream.Collectors.toList;

import com.epam.digital.data.platform.generator.constraints.ConstraintProvider;
import java.util.List;
import org.springframework.stereotype.Component;
import com.epam.digital.data.platform.generator.model.template.Constraint;
import schemacrawler.schema.Column;

@Component
public class CompositeConstraintProvider implements ConstraintProvider {

  private final List<ConstraintProvider> providers;
  private final FormattingConstraintProvider formattingConstraintProvider;
  private final MarshalingConstraintProvider marshalingConstraintProvider;

  public CompositeConstraintProvider(
      List<ConstraintProvider> providers,
      FormattingConstraintProvider formattingConstraintProvider,
      MarshalingConstraintProvider marshalingConstraintProvider) {
    this.providers = providers;
    this.formattingConstraintProvider = formattingConstraintProvider;
    this.marshalingConstraintProvider = marshalingConstraintProvider;
  }

  @Override
  public List<Constraint> getConstraintForProperty(Column column, String propertyClassName) {
    return providers.stream()
        .map(x -> x.getConstraintForProperty(column, propertyClassName))
        .flatMap(List::stream)
        .collect(toList());
  }

  public FormattingConstraintProvider getFormattingConstraintProvider() {
    return formattingConstraintProvider;
  }

  public MarshalingConstraintProvider getMarshalingConstraintProvider() {
    return marshalingConstraintProvider;
  }
}
