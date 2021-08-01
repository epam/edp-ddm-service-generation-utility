package com.epam.digital.data.platform.generator.scope;

public class RbacControllerScope extends ClassScope {

  private String schemaName;
  private String endpoint;
  private String pkName;
  private String pkType;
  private String roleExpression;

  public String getSchemaName() {
    return schemaName;
  }

  public void setSchemaName(String schemaName) {
    this.schemaName = schemaName;
  }

  public String getEndpoint() {
    return endpoint;
  }

  public void setEndpoint(String endpoint) {
    this.endpoint = endpoint;
  }

  public String getPkName() {
    return pkName;
  }

  public void setPkName(String pkName) {
    this.pkName = pkName;
  }

  public String getPkType() {
    return pkType;
  }

  public void setPkType(String pkType) {
    this.pkType = pkType;
  }

  public String getRoleExpression() {
    return roleExpression;
  }

  public void setRoleExpression(String roleExpression) {
    this.roleExpression = roleExpression;
  }
}
