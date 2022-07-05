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

package com.epam.digital.data.platform.generator.model.template;

import java.util.List;

public class NestedSelectableFieldsGroup {
  private String tableName;
  private String pkName;
  private List<SelectableField> fields;

  public NestedSelectableFieldsGroup() {
  }

  public NestedSelectableFieldsGroup(
      String tableName, String pkName, List<SelectableField> fields) {
    this.tableName = tableName;
    this.pkName = pkName;
    this.fields = fields;
  }

  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public String getPkName() {
    return pkName;
  }

  public void setPkName(String pkName) {
    this.pkName = pkName;
  }

  public List<SelectableField> getFields() {
    return fields;
  }

  public void setFields(List<SelectableField> fields) {
    this.fields = fields;
  }
}
