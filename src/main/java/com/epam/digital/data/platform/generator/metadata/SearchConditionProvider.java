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

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.epam.digital.data.platform.generator.model.template.SearchType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class SearchConditionProvider {

  static final String SEARCH_CONDITION_CHANGE_TYPE = "searchCondition";
  static final String SEARCH_CONDITION_COLUMN_ATTRIBUTE = "column";
  static final String LOGIC_OPERATOR_ATTRIBUTE_NAME = "logicOperator";
  static final String LIMIT_ATTRIBUTE_NAME = "limit";
  static final String PAGINATION_ATTRIBUTE_NAME = "pagination";

  static final String EXPOSE = "expose";
  static final String TREMBITA = "trembita";
  static final String EXPOSED_CHANGE_NAME = "searchCondition";

  private final MetadataFacade metadataFacade;

  private final RlsMetadataFacade rlsMetadataFacade;

  private final ObjectMapper mapper;

  public SearchConditionProvider(MetadataFacade metadataFacade, RlsMetadataFacade rlsMetadataFacade, ObjectMapper mapper) {
    this.metadataFacade = metadataFacade;
    this.rlsMetadataFacade = rlsMetadataFacade;
    this.mapper = mapper;
  }

  public SearchConditions findFor(String name) {
    var groupedBySearchParam = metadataFacade
        .findByChangeTypeAndChangeName(SEARCH_CONDITION_CHANGE_TYPE, name)
        .collect(groupingBy(
            Metadata::getName,
            mapping(Metadata::getValue,
                toList())));

    var columnToSearchType = metadataFacade
            .findByChangeTypeAndChangeName(SEARCH_CONDITION_CHANGE_TYPE, name)
            .filter(metadata -> SearchType.typeValues().contains(metadata.getName()))
            .collect(toMap(Metadata::getValue, metadata -> SearchType.findByValue(metadata.getName())));

    var returningColumns = metadataFacade.findByChangeType(name)
        .map(Metadata::getValue).collect(toList());

    var searchOperationTree =
        Optional.ofNullable(groupedBySearchParam.get(LOGIC_OPERATOR_ATTRIBUTE_NAME))
            .map(logicOperator -> logicOperator.get(0))
            .map(this::mapLogicOperatorToObj)
            .orElse(null);

    var scParams = new SearchConditions();
    scParams.setColumnToSearchType(columnToSearchType);
    scParams.setSearchOperationTree(searchOperationTree);
    scParams.setReturningColumns(returningColumns);

    var limit = groupedBySearchParam.get(LIMIT_ATTRIBUTE_NAME);
    if (limit != null) {
      scParams.setLimit(Integer.valueOf(limit.get(0)));
    }

    var pagination =
        Optional.ofNullable(groupedBySearchParam.get(PAGINATION_ATTRIBUTE_NAME))
            .map(paginationValue -> paginationValue.get(0))
            .orElse(null);
    scParams.setPagination(SearchConditionPaginationType.findByValue(pagination));

    return scParams;
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

  public Set<String> getExposedSearchConditions(ExposeSearchConditionOption option) {
    return metadataFacade
        .findByChangeTypeAndChangeNameAndName(EXPOSE, option.getValue(), EXPOSED_CHANGE_NAME)
        .map(Metadata::getValue)
        .collect(Collectors.toSet());
  }

  public RlsMetadata getRlsMetadata(String tableName) {
    return rlsMetadataFacade.findByTypeAndCheckTable(RlsMetadataFacade.METADATA_TYPE_READ, tableName)
            .findFirst().orElse(null);
  }

  public List<RlsMetadata> getRlsMetadata() {
    return rlsMetadataFacade.findByType(RlsMetadataFacade.METADATA_TYPE_READ).collect(toList());
  }

  private SearchConditionOperationTree mapLogicOperatorToObj(String logicOperatorMetadataJsonStr) {
    try {
      return mapper.readValue(logicOperatorMetadataJsonStr, SearchConditionOperationTree.class);
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException("Cannot parse logic operator JSON", e);
    }
  }
}
