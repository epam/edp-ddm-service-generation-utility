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

package com.epam.digital.data.platform.generator.scope;

import com.epam.digital.data.platform.generator.model.template.NestedSelectableFieldsGroup;
import com.epam.digital.data.platform.generator.model.template.RlsFieldRestriction;
import com.epam.digital.data.platform.generator.model.template.SelectableField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryHandlerScope extends ClassScope {

  private String schemaName;
  private String pkColumnName;
  private String tableName;
  private String pkType;
  private String tableDataProviderName;
  private Map<String, List<String>> tableAccessCheckFields = new HashMap<>();
  private List<SelectableField> simpleSelectableFields = new ArrayList<>();
  private Map<String, NestedSelectableFieldsGroup> nestedSingleSelectableGroups = new HashMap<>();
  private Map<String, NestedSelectableFieldsGroup> nestedListSelectableGroups = new HashMap<>();

  private RlsFieldRestriction rls;

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

  public String getTableDataProviderName() {
    return tableDataProviderName;
  }

  public void setTableDataProviderName(String tableDataProviderName) {
    this.tableDataProviderName = tableDataProviderName;
  }

  public Map<String, List<String>> getTableAccessCheckFields() {
    return tableAccessCheckFields;
  }

  public void setTableAccessCheckFields(Map<String, List<String>> tableAccessCheckFields) {
    this.tableAccessCheckFields = tableAccessCheckFields;
  }

  public List<SelectableField> getSimpleSelectableFields() {
    return simpleSelectableFields;
  }

  public void setSimpleSelectableFields(List<SelectableField> simpleSelectableFields) {
    this.simpleSelectableFields = simpleSelectableFields;
  }

  public Map<String, NestedSelectableFieldsGroup> getNestedSingleSelectableGroups() {
    return nestedSingleSelectableGroups;
  }

  public void setNestedSingleSelectableGroups(
      Map<String, NestedSelectableFieldsGroup> nestedSingleSelectableGroups) {
    this.nestedSingleSelectableGroups = nestedSingleSelectableGroups;
  }

  public Map<String, NestedSelectableFieldsGroup> getNestedListSelectableGroups() {
    return nestedListSelectableGroups;
  }

  public void setNestedListSelectableGroups(
      Map<String, NestedSelectableFieldsGroup> nestedListSelectableGroups) {
    this.nestedListSelectableGroups = nestedListSelectableGroups;
  }

  public RlsFieldRestriction getRls() {
    return rls;
  }

  public void setRls(RlsFieldRestriction rls) {
    this.rls = rls;
  }
}
