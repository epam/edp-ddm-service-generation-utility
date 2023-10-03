/*
 * Copyright 2024 EPAM Systems.
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

package com.epam.digital.data.platform.generator.model.template;

public class RlsFieldRestriction {
  private String checkTable;
  private String checkField;
  private String checkColumn;
  private String jwtAttribute;

  public RlsFieldRestriction() {}

  public RlsFieldRestriction(
      String checkTable, String checkField, String checkColumn, String jwtAttribute) {
    this.checkTable = checkTable;
    this.checkField = checkField;
    this.checkColumn = checkColumn;
    this.jwtAttribute = jwtAttribute;
  }

  public String getCheckTable() {
    return checkTable;
  }

  public void setCheckTable(String checkTable) {
    this.checkTable = checkTable;
  }

  public String getCheckField() {
    return checkField;
  }

  public void setCheckField(String checkField) {
    this.checkField = checkField;
  }

  public String getCheckColumn() {
    return checkColumn;
  }

  public void setCheckColumn(String checkColumn) {
    this.checkColumn = checkColumn;
  }

  public String getJwtAttribute() {
    return jwtAttribute;
  }

  public void setJwtAttribute(String jwtAttribute) {
    this.jwtAttribute = jwtAttribute;
  }
}
