package com.epam.digital.data.platform.generator.metadata;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class SearchConditionProvider {

  static final String SEARCH_CONDITION_CHANGE_TYPE = "searchCondition";
  static final String SEARCH_CONDITION_COLUMN_ATTRIBUTE = "column";

  static final String EQUAL_ATTRIBUTE_NAME = "equalColumn";
  static final String STARTS_WITH_ATTRIBUTE_NAME = "startsWithColumn";
  static final String CONTAINS_ATTRIBUTE_NAME = "containsColumn";
  static final String LIMIT_ATTRIBUTE_NAME = "limit";
  static final String PAGINATION_ATTRIBUTE_NAME = "pagination";

  static final String EXPOSE = "expose";
  static final String TREMBITA = "trembita";
  static final String EXPOSED_CHANGE_NAME = "searchCondition";

  private final MetadataFacade metadataFacade;

  public SearchConditionProvider(MetadataFacade metadataFacade) {
    this.metadataFacade = metadataFacade;
  }

  public SearchConditions findFor(String name) {
    var groupedByName = metadataFacade
        .findByChangeTypeAndChangeName(SEARCH_CONDITION_CHANGE_TYPE, name)
        .collect(groupingBy(
            Metadata::getName,
            mapping(Metadata::getValue,
                toList())));

    var b = new SearchConditionsBuilder()
        .equal(groupedByName.getOrDefault(EQUAL_ATTRIBUTE_NAME, emptyList()))
        .startsWith(groupedByName.getOrDefault(STARTS_WITH_ATTRIBUTE_NAME, emptyList()))
        .contains(groupedByName.getOrDefault(CONTAINS_ATTRIBUTE_NAME, emptyList()));

    var limit = groupedByName.get(LIMIT_ATTRIBUTE_NAME);
    if (limit != null) {
      b.limit(Integer.valueOf(limit.get(0)));
    }

    var pagination = groupedByName.get(PAGINATION_ATTRIBUTE_NAME);
    if (pagination != null) {
      b.pagination(Boolean.valueOf(pagination.get(0)));
    }

    return b.build();
  }

  public Map<String, Set<String>> getTableColumnMapFor(String view) {
    var viewColumns = metadataFacade
        .findByChangeTypeAndChangeNameAndName(
            SEARCH_CONDITION_CHANGE_TYPE,
            view,
            SEARCH_CONDITION_COLUMN_ATTRIBUTE)
        .map(Metadata::getValue)
        .collect(toList());

    return metadataFacade.findByChangeTypeAndValueIn(view, viewColumns)
        .collect(groupingBy(
            Metadata::getChangeName,
            mapping(Metadata::getValue,
                toSet())));
  }

  public Set<String> getExposedSearchConditions() {
    return metadataFacade
        .findByChangeTypeAndChangeNameAndName(EXPOSE, TREMBITA, EXPOSED_CHANGE_NAME)
        .map(Metadata::getValue)
        .collect(Collectors.toSet());
  }
}
