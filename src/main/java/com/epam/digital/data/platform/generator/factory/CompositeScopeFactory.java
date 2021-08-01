package com.epam.digital.data.platform.generator.factory;

import static java.util.stream.Collectors.toList;

import com.epam.digital.data.platform.generator.model.Context;
import java.util.List;

public class CompositeScopeFactory implements ScopeFactory {

  private final List<ScopeFactory<?>> scopeFactories;

  public CompositeScopeFactory(List<ScopeFactory<?>> scopeFactories) {
    this.scopeFactories = scopeFactories;
  }

  @Override
  public String getPath() {
    return scopeFactories.get(0).getPath();
  }

  @Override
  public List create(Context context) {
    return scopeFactories.stream()
        .map(x -> x.create(context))
        .flatMap(List::stream)
        .collect(toList());
  }
}
