/*
 * Copyright 2022 EPAM Systems.
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

public class NestedReadEntity {

  private String table;
  private String column;
  private String relatedTable;

  public NestedReadEntity() {
  }

  public NestedReadEntity(String table, String column, String relatedTable) {
    this.table = table;
    this.column = column;
    this.relatedTable = relatedTable;
  }

  public String getTable() {
    return table;
  }

  public void setTable(String table) {
    this.table = table;
  }

  public String getColumn() {
    return column;
  }

  public void setColumn(String column) {
    this.column = column;
  }

  public String getRelatedTable() {
    return relatedTable;
  }

  public void setRelatedTable(String relatedTable) {
    this.relatedTable = relatedTable;
  }
}
