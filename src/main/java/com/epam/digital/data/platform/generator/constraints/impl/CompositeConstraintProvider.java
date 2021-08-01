package com.epam.digital.data.platform.generator.constraints.impl;

import static java.util.stream.Collectors.toList;

import com.epam.digital.data.platform.generator.constraints.ConstraintProvider;
import java.util.List;
import org.springframework.stereotype.Component;
import com.epam.digital.data.platform.generator.model.template.Constraint;

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
  public List<Constraint> getConstraintForProperty(String... propertyValues) {
    return providers.stream()
        .map(x -> x.getConstraintForProperty(propertyValues))
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
