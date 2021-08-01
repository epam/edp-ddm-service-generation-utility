package com.epam.digital.data.platform.generator.metadata;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class SearchConditions {

  private final List<String> equal;
  private final List<String> startsWith;
  private final List<String> contains;
  private final List<String> all;
  private final Integer limit;
  private final Boolean pagination;

  SearchConditions(List<String> equal, List<String> startsWith,
      List<String> contains, Integer limit, Boolean pagination) {
    this.equal = unmodifiableList(equal);
    this.startsWith = unmodifiableList(startsWith);
    this.contains = unmodifiableList(contains);
    this.limit = limit;
    this.pagination = pagination;

    var allSC = new ArrayList<String>();
    Stream.of(equal, startsWith, contains).forEach(allSC::addAll);
    this.all = unmodifiableList(allSC);
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

  public Integer getLimit() {
    return limit;
  }

  public Boolean getPagination() {
    return pagination;
  }

  public List<String> getAll() {
    return all;
  }
}
