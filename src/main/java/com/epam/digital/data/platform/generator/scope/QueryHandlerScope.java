package com.epam.digital.data.platform.generator.scope;

import com.epam.digital.data.platform.generator.model.template.SelectableField;

import java.util.List;

public class QueryHandlerScope extends ClassScope {

  private String schemaName;
  private String pkColumnName;
  private String tableName;
  private String pkType;
  private List<SelectableField> selectableFields;

  public String getSchemaName() {
    return schemaName;
  }

  public void setSchemaName(String schemaName) {
    this.schemaName = schemaName;
  }

  public String getPkColumnName() {
    return pkColumnName;
  }

  public void setPkColumnName(String pkColumnName) {
    this.pkColumnName = pkColumnName;
  }

  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public String getPkType() {
    return pkType;
  }

  public void setPkType(String pkType) {
    this.pkType = pkType;
  }

  public List<SelectableField> getOutputFields() {
    return selectableFields;
  }

  public void setOutputFields(List<SelectableField> selectableFields) {
    this.selectableFields = selectableFields;
  }
}
