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
import static org.mockito.BDDMockito.given;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AsyncDataProviderTest {

  static final String READ_MODE_CHANGE_TYPE = "readMode";
  static final String SEARCH_CONDITION_CHANGE_TYPE = "searchCondition";

  AsyncDataProvider instance;

  @Mock
  private MetadataRepository metadataRepository;

  @BeforeEach
  void setUp() {
    given(metadataRepository.findAll()).willReturn(generateMetadata());
    instance = new AsyncDataProvider(new MetadataFacade(metadataRepository));
  }

  private List<Metadata> generateMetadata() {
    return List.of(
        new Metadata(1L, READ_MODE_CHANGE_TYPE, "createTable", "table_name", "async"),
        new Metadata(2L, READ_MODE_CHANGE_TYPE, "createSearchCondition", "sc_name", "async"),
        new Metadata(3L, READ_MODE_CHANGE_TYPE, "createSimpleSearchCondition", "ssc_name", "async"),
        new Metadata(4L, SEARCH_CONDITION_CHANGE_TYPE, "sc_name", "column", "value")
    );
  }

  @Test
  void shouldFindAsyncData() {
    var asyncData = instance.findAll();
    assertThat(asyncData.getAsyncTables().size()).isEqualTo(1);
    Assertions.assertTrue(asyncData.getAsyncTables().contains("table_name"));
    assertThat(asyncData.getAsyncSearchConditions().size()).isEqualTo(2);
    Assertions.assertTrue(asyncData.getAsyncSearchConditions().contains("sc_name_v"));
    Assertions.assertTrue(asyncData.getAsyncSearchConditions().contains("ssc_name_v"));
  }
}
