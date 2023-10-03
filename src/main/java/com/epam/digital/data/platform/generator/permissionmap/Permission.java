/*
 * Copyright 2021 EPAM Systems.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

  private String objectType;

  @Column("role_name")
  private String role;

  public Permission(Long id, String tableName, String columnName, String operation, String role, String objectType) {
    this.id = id;
    this.tableName = tableName;
    this.columnName = columnName;
    this.operation = operation;
    this.role = role;
    this.objectType = objectType;
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

  public String getObjectType() {return objectType; }

  @Override
  public String toString() {
    return "Permission{id=" + id + ", tableName='" + tableName + '\''
        + ", columnName='" + columnName + '\'' + ", operation='" + operation + '\''
        + ", role='" + role + '\'' + ", objectType='" + objectType + '\'' + '}';
  }
}
