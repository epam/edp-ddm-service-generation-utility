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

package com.epam.digital.data.platform.generator.metadata;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("ddm_rls_metadata")
public class RlsMetadata {

  @Id
  @Column("rls_id")
  private Long id;

  @Column("name")
  private String name;
  @Column("type")
  private String type;
  @Column("jwt_attribute")
  private String jwtAttribute;
  @Column("check_column")
  private String checkColumn;
  @Column("check_table")
  private String checkTable;

  public RlsMetadata(Long id, String name, String type, String jwtAttribute, String checkColumn, String checkTable) {
    this.id = id;
    this.name = name;
    this.type = type;
    this.jwtAttribute = jwtAttribute;
    this.checkColumn = checkColumn;
    this.checkTable = checkTable;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }

  public String getJwtAttribute() {
    return jwtAttribute;
  }

  public String getCheckColumn() {
    return checkColumn;
  }

  public String getCheckTable() {
    return checkTable;
  }


  @Override
  public String toString() {
    return "RlsMetadata{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", type='" + type + '\'' +
            ", jwt_attribute='" + jwtAttribute + '\'' +
            ", check_column='" + checkColumn + '\'' +
            ", check_table='" + checkTable + '\'' +
            '}';
  }
}
