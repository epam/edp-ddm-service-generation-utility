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

import java.util.List;
import java.util.Map;

import com.epam.digital.data.platform.generator.metadata.SearchConditionPaginationType;
import com.epam.digital.data.platform.generator.model.template.NestedSelectableFieldsGroup;
import com.epam.digital.data.platform.generator.model.template.RlsFieldRestriction;
import com.epam.digital.data.platform.generator.model.template.SearchOperation;
import com.epam.digital.data.platform.generator.model.template.SelectableField;

public class SearchHandlerScope extends ClassScope {

  private String schemaName;
  private Integer limit;
  private String tableName;
  private List<SearchOperation> searchLogicOperations;
  private List<String> enumSearchConditionFields;
  private List<SelectableField> simpleSelectableFields;
  private SearchConditionPaginationType pagination;
  private Map<String, NestedSelectableFieldsGroup> nestedSingleSelectableGroups;
  private Map<String, NestedSelectableFieldsGroup> nestedListSelectableGroups;

  private RlsFieldRestriction rls;

  public String getSchemaName() {
    return schemaName;
  }

  public void setSchemaName(String schemaName) {
    this.schemaName = schemaName;
  }

  public Integer getLimit() {
    return limit;
  }

  public void setLimit(Integer limit) {
    this.limit = limit;
  }

  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public List<SearchOperation> getSearchLogicOperations() {
    return searchLogicOperations;
  }

  public void setSearchLogicOperations(List<SearchOperation> searchLogicOperations) {
    this.searchLogicOperations = searchLogicOperations;
  }

  public List<String> getEnumSearchConditionFields() {
    return enumSearchConditionFields;
  }

  public void setEnumSearchConditionFields(List<String> enumSearchConditionFields) {
    this.enumSearchConditionFields = enumSearchConditionFields;
  }

  public List<SelectableField> getSimpleSelectableFields() {
    return simpleSelectableFields;
  }

  public void setSimpleSelectableFields(List<SelectableField> selectableFields) {
    this.simpleSelectableFields = selectableFields;
  }

  public SearchConditionPaginationType getPagination() {
    return pagination;
  }

  public void setPagination(SearchConditionPaginationType pagination) {
    this.pagination = pagination;
  }

  public Map<String, NestedSelectableFieldsGroup> getNestedSingleSelectableGroups() {
    return nestedSingleSelectableGroups;
  }

  public void setNestedSingleSelectableGroups(Map<String, NestedSelectableFieldsGroup> nestedSingleSelectableGroups) {
    this.nestedSingleSelectableGroups = nestedSingleSelectableGroups;
  }

  public Map<String, NestedSelectableFieldsGroup> getNestedListSelectableGroups() {
    return nestedListSelectableGroups;
  }

  public void setNestedListSelectableGroups(Map<String, NestedSelectableFieldsGroup> nestedListSelectableGroups) {
    this.nestedListSelectableGroups = nestedListSelectableGroups;
  }

  public RlsFieldRestriction getRls() {
    return rls;
  }

  public void setRls(RlsFieldRestriction rls) {
    this.rls = rls;
  }
}
