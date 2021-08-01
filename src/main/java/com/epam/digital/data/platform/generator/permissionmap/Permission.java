package com.epam.digital.data.platform.generator.permissionmap;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("ddm_role_permission")
class Permission {

  @Id
  @Column("permission_id")
  private Long id;

  @Column("object_name")
  private String tableName;

  private String columnName;

  private String operation;

  @Column("role_name")
  private String role;

  public Permission(Long id, String tableName, String columnName, String operation, String role) {
    this.id = id;
    this.tableName = tableName;
    this.columnName = columnName;
    this.operation = operation;
    this.role = role;
  }

  public Long getId() {
    return id;
  }

  public String getTableName() {
    return tableName;
  }

  public String getColumnName() {
    return columnName;
  }

  public String getOperation() {
    return operation;
  }

  public String getRole() {
    return role;
  }

  @Override
  public String toString() {
    return "Permission{" +
        "id=" + id +
        ", tableName='" + tableName + '\'' +
        ", columnName='" + columnName + '\'' +
        ", operation='" + operation + '\'' +
        ", role='" + role + '\'' +
        '}';
  }
}
