package com.epam.digital.data.platform.generator.factory;

import static java.util.stream.Collectors.toList;

import com.epam.digital.data.platform.generator.model.Context;
import java.util.List;
import schemacrawler.schema.Table;

public abstract class MultiAbstractScope<T> extends AbstractScope<T> {

  protected abstract T map(Table table, Context context);

  protected abstract boolean isApplicable(Table table);

  @Override
  public List<T> create(Context context) {
    return context.getCatalog().getTables().stream()
        .filter(this::isApplicable)
        .map(t -> map(t, context))
        .collect(toList());
  }
}
