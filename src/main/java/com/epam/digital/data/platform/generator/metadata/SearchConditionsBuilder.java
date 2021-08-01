package com.epam.digital.data.platform.generator.metadata;

import static java.util.Collections.emptyList;

import java.util.List;

public class SearchConditionsBuilder {

  private List<String> equal = emptyList();
  private List<String> startsWith = emptyList();
  private List<String> contains = emptyList();
  private Integer limit;
  private Boolean pagination;

  public SearchConditionsBuilder equal(List<String> equal) {
    this.equal = equal;
    return this;
  }

  public SearchConditionsBuilder startsWith(List<String> startsWith) {
    this.startsWith = startsWith;
    return this;
  }

  public SearchConditionsBuilder contains(List<String> contains) {
    this.contains = contains;
    return this;
  }

  public SearchConditionsBuilder limit(Integer limit) {
    this.limit = limit;
    return this;
  }

  public SearchConditionsBuilder pagination(Boolean pagination) {
    this.pagination = pagination;
    return this;
  }

  public SearchConditions build() {
    return new SearchConditions(equal, startsWith, contains, limit, pagination);
  }
}
