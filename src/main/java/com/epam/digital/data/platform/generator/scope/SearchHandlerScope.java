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

import com.epam.digital.data.platform.generator.model.template.SelectableField;
import com.epam.digital.data.platform.generator.model.template.SearchConditionField;

public class SearchHandlerScope extends ClassScope {

  private String schemaName;
  private Integer limit;
  private String tableName;
  private List<SearchConditionField> searchConditionFields;
  private List<String> enumSearchConditionFields;
  private List<SelectableField> selectableFields;
  private Boolean pagination;

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

  public List<SearchConditionField> getSearchConditionFields() {
    return searchConditionFields;
  }

  public void setSearchConditionFields(List<SearchConditionField> searchConditionFields) {
    this.searchConditionFields = searchConditionFields;
  }

  public List<String> getEnumSearchConditionFields() {
    return enumSearchConditionFields;
  }

  public void setEnumSearchConditionFields(List<String> enumSearchConditionFields) {
    this.enumSearchConditionFields = enumSearchConditionFields;
  }

  public List<SelectableField> getOutputFields() {
    return selectableFields;
  }

  public void setOutputFields(List<SelectableField> selectableFields) {
    this.selectableFields = selectableFields;
  }

  public Boolean getPagination() {
    return pagination;
  }

  public void setPagination(Boolean pagination) {
    this.pagination = pagination;
  }
}
