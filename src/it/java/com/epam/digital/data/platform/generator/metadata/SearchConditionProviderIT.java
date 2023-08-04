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

import static org.assertj.core.api.Assertions.assertThat;

import com.epam.digital.data.platform.generator.config.TestConfig;
import com.epam.digital.data.platform.generator.model.template.SearchOperatorType;
import com.epam.digital.data.platform.generator.model.template.SearchType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.epam.digital.data.platform.generator.config.DataSourceEnable;

import java.util.List;

@DataSourceEnable
@SpringBootTest(
    classes = {
      SearchConditionProvider.class,
      MetadataFacade.class,
      RlsMetadataFacade.class,
      TestConfig.class
    })
class SearchConditionProviderIT {

  @Autowired
  private SearchConditionProvider instance;


  @Test
  void shouldFindByChangeName() {
    var sc = instance.findFor("pd_processing_consent_2");

    assertThat(sc.getColumnToSearchType())
        .hasSize(5)
        .containsEntry("person_full_name", SearchType.EQUAL)
        .containsEntry("person_full_name2", SearchType.STARTS_WITH)
        .containsEntry("person_pass_number", SearchType.EQUAL)
        .containsEntry("person_full_name3", SearchType.CONTAINS)
        .containsEntry("person_full_name4", SearchType.STARTS_WITH_ARRAY);
    assertThat(sc.getSearchOperationTree())
            .usingRecursiveComparison().isEqualTo(createSearchTree());
    assertThat(sc.getLimit()).isEqualTo(1);
  }

  private SearchConditionOperationTree createSearchTree() {
    var searchTree = new SearchConditionOperationTree();

    var operation = new SearchConditionOperation();
    operation.setTableName("pd_processing_consent");

    var logicOperator1 = new SearchConditionOperation.LogicOperator();
    logicOperator1.setType(SearchOperatorType.OR);
    logicOperator1.setColumns(List.of("person_pass_number"));

    var logicOperator2 = new SearchConditionOperation.LogicOperator();
    logicOperator2.setType(SearchOperatorType.AND);

    logicOperator2.setColumns(List.of("person_full_name3", "person_full_name4"));

    logicOperator1.setLogicOperators(List.of(logicOperator2));

    operation.setLogicOperators(List.of(logicOperator1));
    searchTree.setOperations(List.of(operation));

    return searchTree;
  }
}
