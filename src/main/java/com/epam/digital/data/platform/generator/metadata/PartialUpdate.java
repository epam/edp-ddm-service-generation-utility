package com.epam.digital.data.platform.generator.metadata;

import java.util.Set;

public class PartialUpdate {

  private final String name;
  private final String tableName;
  private final Set<String> columns;

  public PartialUpdate(String name, String tableName, Set<String> columns) {
    this.tableName = tableName;
    this.name = name;
    this.columns = columns;
  }

  public String getName() {
    return name;
  }

  public String getTableName() {
    return tableName;
  }

  public Set<String> getColumns() {
    return columns;
  }

  @Override
  public String toString() {
    return "PartialUpdate{" +
        "name='" + name + '\'' +
        ", tableName='" + tableName + '\'' +
        ", columns=" + columns +
        '}';
  }
}
