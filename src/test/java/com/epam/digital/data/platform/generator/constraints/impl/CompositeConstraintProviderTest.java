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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class CompositeConstraintProviderTest {

  FormattingConstraintProvider formattingConstraintProvider = new FormattingConstraintProvider();
  MarshalingConstraintProvider marshalingConstraintProvider = new MarshalingConstraintProvider();

  CompositeConstraintProvider instance = new CompositeConstraintProvider(List.of(
      formattingConstraintProvider, marshalingConstraintProvider,
      new ValidationConstraintProvider()
  ), formattingConstraintProvider, marshalingConstraintProvider);

  @Test
  void shouldFindConstraintsInAllProviders() {
    assertThat(instance.getConstraintForProperty("timestamp with time zone", "java.time.LocalDateTime")).hasSize(2);
    assertThat(instance.getConstraintForProperty( "dn_edrpou", "java.lang.String")).hasSize(1);
  }
}
