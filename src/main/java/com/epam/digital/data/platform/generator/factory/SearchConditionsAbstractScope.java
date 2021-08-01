package com.epam.digital.data.platform.generator.factory;

import schemacrawler.schema.Table;

public abstract class SearchConditionsAbstractScope<T> extends MultiAbstractScope<T> {

  @Override
  protected boolean isApplicable(Table table) {
    return isSearchConditionsView(table);
  }
}
