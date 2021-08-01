package com.epam.digital.data.platform.generator.model;

import schemacrawler.schema.Catalog;

public final class Context {

  private final Blueprint blueprint;

  private final Catalog catalog;

  public Context(
      Blueprint blueprint, Catalog catalog) {
    this.blueprint = blueprint;
    this.catalog = catalog;
  }

  public Blueprint getBlueprint() {
    return blueprint;
  }

  public Catalog getCatalog() {
    return catalog;
  }
}
