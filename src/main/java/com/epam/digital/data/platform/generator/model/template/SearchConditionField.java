package com.epam.digital.data.platform.generator.model.template;

public class SearchConditionField {

  private String name;
  private String columnName;
  private String operation;

  public SearchConditionField(String name, String columnName, String operation) {
    this.name = name;
    this.columnName = columnName;
    this.operation = operation;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getColumnName() {
    return columnName;
  }

  public void setColumnName(String columnName) {
    this.columnName = columnName;
  }

  public String getOperation() {
    return operation;
  }

  public void setOperation(String operation) {
    this.operation = operation;
  }
}
