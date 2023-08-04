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

import static com.epam.digital.data.platform.generator.metadata.SearchConditionProvider.*;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.Collections;
import java.util.List;

import com.epam.digital.data.platform.generator.model.template.SearchOperatorType;
import com.epam.digital.data.platform.generator.model.template.SearchType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SearchConditionProviderTest {

  private SearchConditionProvider instance;

  @Mock
  private MetadataRepository metadataRepository;

  @Mock
  private RlsMetadataRepository rlsMetadataRepository;

  private final ObjectMapper objectMapper =
      new ObjectMapper().enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);

  @Nested
  class FindFor {

    static final String CHANGE_NAME = "some changeName";
    static final String TABLE_NAME = "table";

    @BeforeEach
    public void setUp() {
      setupSearchConditionsFound(generateMetadata(), generateRlsMetadata());
    }

    private void setupSearchConditionsFound(List<Metadata> metadata, List<RlsMetadata> rlsMetadata) {
      given(metadataRepository.findAll()).willReturn(metadata);
      given(rlsMetadataRepository.findAll()).willReturn(rlsMetadata);
      instance =
          new SearchConditionProvider(
              new MetadataFacade(metadataRepository),
              new RlsMetadataFacade(rlsMetadataRepository),
              objectMapper);
    }

    private List<Metadata> generateMetadata() {
      return List.of(
          new Metadata(
              1L,
              SEARCH_CONDITION_CHANGE_TYPE,
              CHANGE_NAME,
              SearchType.EQUAL.getValue(),
              "whatever1"),
          new Metadata(
              2L,
              SEARCH_CONDITION_CHANGE_TYPE,
              CHANGE_NAME,
              SearchType.EQUAL.getValue(),
              "whatever2"),
          new Metadata(
              3L,
              SEARCH_CONDITION_CHANGE_TYPE,
              CHANGE_NAME,
              SearchType.STARTS_WITH.getValue(),
              "whatever3"),
          new Metadata(
              4L,
              SEARCH_CONDITION_CHANGE_TYPE,
              CHANGE_NAME,
              SearchType.STARTS_WITH.getValue(),
              "whatever4"),
          new Metadata(
              5L,
              SEARCH_CONDITION_CHANGE_TYPE,
              CHANGE_NAME,
              SearchType.CONTAINS.getValue(),
              "whatever5"),
          new Metadata(
              6L,
              SEARCH_CONDITION_CHANGE_TYPE,
              CHANGE_NAME,
              SearchType.CONTAINS.getValue(),
              "whatever6"),
          new Metadata(7L, SEARCH_CONDITION_CHANGE_TYPE, CHANGE_NAME, LIMIT_ATTRIBUTE_NAME, "7"),
          new Metadata(8L, CHANGE_NAME, TABLE_NAME, SEARCH_CONDITION_COLUMN_ATTRIBUTE, "alias1"),
          new Metadata(9L, CHANGE_NAME, TABLE_NAME, SEARCH_CONDITION_COLUMN_ATTRIBUTE, "alias2"),
          new Metadata(
              10L,
              SEARCH_CONDITION_CHANGE_TYPE,
              CHANGE_NAME,
              SearchType.STARTS_WITH_ARRAY.getValue(),
              "whatever10"),
          new Metadata(
              11L,
              SEARCH_CONDITION_CHANGE_TYPE,
              CHANGE_NAME,
              LOGIC_OPERATOR_ATTRIBUTE_NAME,
              String.format(
                  "{\"operations\":[{\"tableName\":\"%s\",\"logicOperators\":[{\"type\":\"or\",\"columns\":[\"%s\",\"%s\"],\"logicOperators\":[]}]}]}",
                  TABLE_NAME, "whatever1", "whatever2")));
    }

    @Test
    void shouldCollectSCSearchTypesByChangeName() {
      var map = instance.findFor(CHANGE_NAME).getColumnToSearchType();

      assertThat(map)
          .hasSize(7)
          .containsEntry("whatever1", SearchType.EQUAL)
          .containsEntry("whatever2", SearchType.EQUAL)
          .containsEntry("whatever3", SearchType.STARTS_WITH)
          .containsEntry("whatever4", SearchType.STARTS_WITH)
          .containsEntry("whatever10", SearchType.STARTS_WITH_ARRAY)
          .containsEntry("whatever5", SearchType.CONTAINS)
          .containsEntry("whatever6", SearchType.CONTAINS);
    }

    @Test
    void shouldCollectSCOperationTreeByChangeName() {
      var expectedTree = new SearchConditionOperationTree();
      var operation = new SearchConditionOperation();
      operation.setTableName(TABLE_NAME);
      var logicOperator = new SearchConditionOperation.LogicOperator();
      logicOperator.setType(SearchOperatorType.OR);
      logicOperator.setColumns(List.of("whatever1", "whatever2"));
      operation.setLogicOperators(List.of(logicOperator));
      expectedTree.setOperations(List.of(operation));

      var actualTree = instance.findFor(CHANGE_NAME).getSearchOperationTree();

      assertThat(actualTree).usingRecursiveComparison().isEqualTo(expectedTree);
    }

    @Test
    void shouldCollectReturningColumnsByChangeType() {
      var list = instance.findFor(CHANGE_NAME).getReturningColumns();

      assertThat(list).hasSize(2);
      assertThat(list.get(0)).isEqualTo("alias1");
      assertThat(list.get(1)).isEqualTo("alias2");
    }

    @Test
    void shouldCollectLimitSCByChangeName() {
      assertThat(instance.findFor(CHANGE_NAME).getLimit()).isEqualTo(7);
    }

    @Test
    void shouldReturnEmptyListWhenNoSCDefined() {
      setupSearchConditionsFound(emptyList(), emptyList());

      assertThat(instance.findFor(CHANGE_NAME).getColumnToSearchType()).isEmpty();
    }

    @Test
    void shouldReturnLimitNullWhenNoLimitInSCDefined() {
      setupSearchConditionsFound(emptyList(), emptyList());

      assertThat(instance.findFor(CHANGE_NAME).getLimit()).isNull();
    }
  }

  @Nested
  class GetTableColumnMapFor {

    static final String VIEW_NAME = "koatuu_equal_koatuu_id_name";

    static final String TABLE_1_NAME = "koatuu";
    static final String TABLE_2_NAME = "koatuu_obl";

    static final String COLUMN_1 = "koatuu_id";
    static final String COLUMN_2 = "name";
    static final String VIEW_COLUMN_1 = "koatuu_id_obl";
    static final String VIEW_COLUMN_2 = "name_obl";

    @BeforeEach
    public void setUp() {
      setupSearchConditionsFound(generateMetadata(), generateRlsMetadata());
    }

    private void setupSearchConditionsFound(List<Metadata> metadata, List<RlsMetadata> rlsMetadata) {
      given(metadataRepository.findAll()).willReturn(metadata);
      given(rlsMetadataRepository.findAll()).willReturn(rlsMetadata);
      instance =
          new SearchConditionProvider(
              new MetadataFacade(metadataRepository),
              new RlsMetadataFacade(rlsMetadataRepository),
              objectMapper);
    }

    private List<Metadata> generateMetadata() {
      return List.of(
          new Metadata(1L, SEARCH_CONDITION_CHANGE_TYPE, VIEW_NAME,
              SEARCH_CONDITION_COLUMN_ATTRIBUTE, COLUMN_1),
          new Metadata(1L, SEARCH_CONDITION_CHANGE_TYPE, VIEW_NAME,
              SEARCH_CONDITION_COLUMN_ATTRIBUTE, COLUMN_2),
          new Metadata(1L, SEARCH_CONDITION_CHANGE_TYPE, VIEW_NAME,
              SEARCH_CONDITION_COLUMN_ATTRIBUTE, VIEW_COLUMN_1),
          new Metadata(1L, SEARCH_CONDITION_CHANGE_TYPE, VIEW_NAME,
              SEARCH_CONDITION_COLUMN_ATTRIBUTE, VIEW_COLUMN_2),

          new Metadata(1L, VIEW_NAME, TABLE_1_NAME, COLUMN_1, COLUMN_1),
          new Metadata(1L, VIEW_NAME, TABLE_1_NAME, COLUMN_2, COLUMN_2),
          new Metadata(1L, VIEW_NAME, TABLE_2_NAME, COLUMN_1, VIEW_COLUMN_1),
          new Metadata(1L, VIEW_NAME, TABLE_2_NAME, COLUMN_2, VIEW_COLUMN_2)
      );
    }

    @Test
    void shouldMapViewOnTablesAndColumns() {
      var map = instance.getTableColumnMapFor(VIEW_NAME);

      assertThat(map).hasSize(2);
      assertThat(map.get(TABLE_1_NAME)).containsExactly(COLUMN_1, COLUMN_2);
      assertThat(map.get(TABLE_2_NAME)).containsExactly(VIEW_COLUMN_1, VIEW_COLUMN_2);
    }
  }

  @Nested
  class GetExposedSearchConditionViews {

    static final String EXPOSED_VIEW = "search_condition_exposed_for_trembita";
    static final String EXPOSED_PUBLIC_VIEW = "search_condition_exposed_for_public_access";
    static final String SOME_VIEW_NAME = "some_search_condition";

    @BeforeEach
    public void setUp() {
      setupSearchConditionsFound(generateMetadata(), generateRlsMetadata());
    }

    private void setupSearchConditionsFound(List<Metadata> metadata, List<RlsMetadata> rlsMetadata) {
      given(metadataRepository.findAll()).willReturn(metadata);
      given(rlsMetadataRepository.findAll()).willReturn(rlsMetadata);
      instance =
          new SearchConditionProvider(
              new MetadataFacade(metadataRepository),
              new RlsMetadataFacade(rlsMetadataRepository),
              objectMapper);
    }

    private List<Metadata> generateMetadata() {
      return List.of(
          new Metadata(1L, EXPOSE, TREMBITA, EXPOSED_CHANGE_NAME, EXPOSED_VIEW),
          new Metadata(2L, EXPOSE, "not_" + TREMBITA, EXPOSED_CHANGE_NAME, SOME_VIEW_NAME),
          new Metadata(1L, EXPOSE, "publicAccess", EXPOSED_CHANGE_NAME, EXPOSED_PUBLIC_VIEW)
      );
    }


    @Test
    void shouldMapViewOnTablesAndColumns() {
      var set = instance.getExposedSearchConditions(ExposeSearchConditionOption.TREMBITA);

      assertThat(set).hasSize(1);
      assertThat(set.stream().findFirst().get()).isEqualTo(EXPOSED_VIEW);
    }

    @Test
    void shouldGetExposedSearchConditionForPublicAccess() {
      var set = instance.getExposedSearchConditions(ExposeSearchConditionOption.PUBLIC_ACCESS);

      assertThat(set).hasSize(1);
      assertThat(set.stream().findFirst()).contains(EXPOSED_PUBLIC_VIEW);
    }
  }

  @Nested
  class GetRlsInfo {

    @BeforeEach
    public void setUp() {
      setupSearchConditionsFound(Collections.emptyList(), generateRlsMetadata());
    }

    private void setupSearchConditionsFound(List<Metadata> metadata, List<RlsMetadata> rlsMetadata) {
      given(metadataRepository.findAll()).willReturn(metadata);
      given(rlsMetadataRepository.findAll()).willReturn(rlsMetadata);
      instance =
          new SearchConditionProvider(
              new MetadataFacade(metadataRepository),
              new RlsMetadataFacade(rlsMetadataRepository),
              objectMapper);
    }

    @Test
    void shouldReturnRlsInfo() {
      var lst = instance.getRlsMetadata();

      assertThat(lst).hasSize(2);

      assertThat(lst.get(0).getCheckTable()).isEqualTo("table1");
      assertThat(lst.get(1).getCheckTable()).isEqualTo("table2");
    }
  }

  private List<RlsMetadata> generateRlsMetadata() {
    return List.of(
            new RlsMetadata(1L, "test1", "read", "katottg", "col1", "table1"),
            new RlsMetadata(2L, "test2", "read", "katottg", "col2", "table2")
    );
  }
}
