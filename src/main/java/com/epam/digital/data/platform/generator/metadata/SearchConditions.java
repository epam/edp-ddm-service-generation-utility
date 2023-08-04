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

import com.epam.digital.data.platform.generator.model.template.SearchType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchConditions {

  private List<String> returningColumns = new ArrayList<>();
  private Map<String, SearchType> columnToSearchType = new HashMap<>();
  private SearchConditionOperationTree searchOperationTree;
  private Integer limit;
  private SearchConditionPaginationType pagination;

  public List<String> getReturningColumns() {
    return returningColumns;
  }

  public void setReturningColumns(List<String> returningColumns) {
    this.returningColumns = returningColumns;
  }

  public Map<String, SearchType> getColumnToSearchType() {
    return columnToSearchType;
  }

  public void setColumnToSearchType(Map<String, SearchType> columnToSearchType) {
    this.columnToSearchType = columnToSearchType;
  }

  public SearchConditionOperationTree getSearchOperationTree() {
    return searchOperationTree;
  }

  public void setSearchOperationTree(SearchConditionOperationTree searchOperationTree) {
    this.searchOperationTree = searchOperationTree;
  }

  public Integer getLimit() {
    return limit;
  }

  public void setLimit(Integer limit) {
    this.limit = limit;
  }

  public SearchConditionPaginationType getPagination() {
    return pagination;
  }

  public void setPagination(SearchConditionPaginationType pagination) {
    this.pagination = pagination;
  }
}
