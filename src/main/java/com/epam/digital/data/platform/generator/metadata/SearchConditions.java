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

import java.util.List;

import static java.util.Collections.unmodifiableList;

public class SearchConditions {

  private final List<String> equal;
  private final List<String> startsWith;
  private final List<String> contains;
  private final List<String> in;
  private final List<String> returningColumns;
  private final Integer limit;
  private final Boolean pagination;

  SearchConditions(List<String> equal, List<String> startsWith,
      List<String> contains, List<String> in, List<String> returningColumns, Integer limit, Boolean pagination) {
    this.equal = unmodifiableList(equal);
    this.startsWith = unmodifiableList(startsWith);
    this.contains = unmodifiableList(contains);
    this.returningColumns = unmodifiableList(returningColumns);
    this.in = in;
    this.limit = limit;
    this.pagination = pagination;
  }

  public List<String> getEqual() {
    return equal;
  }

  public List<String> getStartsWith() {
    return startsWith;
  }

  public List<String> getContains() {
    return contains;
  }

  public List<String> getIn() {
    return in;
  }

  public List<String> getReturningColumns() {return returningColumns;}

  public Integer getLimit() {
    return limit;
  }

  public Boolean getPagination() {
    return pagination;
  }
}
