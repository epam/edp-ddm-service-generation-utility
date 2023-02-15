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

import static java.util.Collections.emptyList;

import java.util.List;

public class SearchConditionsBuilder {

  private List<String> equal = emptyList();
  private List<String> notEqual = emptyList();
  private List<String> startsWith = emptyList();
  private List<String> startsWithArray = emptyList();
  private List<String> contains = emptyList();
  private List<String> in = emptyList();
  private List<String> notIn = emptyList();
  private List<String> between = emptyList();
  private List<String> returningColumns = emptyList();
  private Integer limit;
  private SearchConditionPaginationType pagination;

  public SearchConditionsBuilder equal(List<String> equal) {
    this.equal = equal;
    return this;
  }

  public SearchConditionsBuilder notEqual(List<String> notEqual) {
    this.notEqual = notEqual;
    return this;
  }

  public SearchConditionsBuilder startsWith(List<String> startsWith) {
    this.startsWith = startsWith;
    return this;
  }
  public SearchConditionsBuilder startsWithArray(List<String> startsWithArray) {
    this.startsWithArray = startsWithArray;
    return this;
  }

  public SearchConditionsBuilder contains(List<String> contains) {
    this.contains = contains;
    return this;
  }

  public SearchConditionsBuilder in(List<String> in) {
    this.in = in;
    return this;
  }

  public SearchConditionsBuilder notIn(List<String> notIn) {
    this.notIn = notIn;
    return this;
  }

  public SearchConditionsBuilder between(List<String> between) {
    this.between = between;
    return this;
  }

  public SearchConditionsBuilder returningColumns(List<String> returningColumns) {
    this.returningColumns = returningColumns;
    return this;
  }

  public SearchConditionsBuilder limit(Integer limit) {
    this.limit = limit;
    return this;
  }

  public SearchConditionsBuilder pagination(SearchConditionPaginationType pagination) {
    this.pagination = pagination;
    return this;
  }

  public SearchConditions build() {
    return new SearchConditions(
        equal, notEqual, startsWith, startsWithArray, contains, in, notIn, between, returningColumns, limit, pagination);
  }
}
