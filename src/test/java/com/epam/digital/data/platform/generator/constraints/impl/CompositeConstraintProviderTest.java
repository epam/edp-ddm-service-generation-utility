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
    assertThat(instance.getConstraintForProperty("java.time.LocalDateTime")).hasSize(2);
    assertThat(instance.getConstraintForProperty("dn_edrpou")).hasSize(1);
  }
}
