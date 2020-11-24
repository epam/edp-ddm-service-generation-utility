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

import static com.epam.digital.data.platform.generator.metadata.PartialUpdateProvider.PARTIAL_UPDATE_CHANGE_TYPE;
import static com.epam.digital.data.platform.generator.utils.TestUtils.ignoringCollectionOrder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PartialUpdateProviderTest {

  static final String UPDATE_1 = "my_table_upd";
  static final String UPDATE_2 = "my_table_another_upd";
  static final String UPDATE_3 = "another_table_upd";
  static final String TABLE_1 = "my_table";
  static final String TABLE_2 = "another_table";
  static final String COLUMN_1 = "my_column";
  static final String COLUMN_2 = "one_more_column";
  static final String COLUMN_3 = "another_column";

  PartialUpdateProvider instance;

  @Mock
  private MetadataRepository metadataRepository;

  @BeforeEach
  void setUp() {
    setupEnumMetadata(generateMetadata());
  }

  private void setupEnumMetadata(List<Metadata> metadata) {
    given(metadataRepository.findAll()).willReturn(metadata);
    instance = new PartialUpdateProvider(new MetadataFacade(metadataRepository));
  }

  private List<Metadata> generateMetadata() {
    return List.of(
        new Metadata(1L, PARTIAL_UPDATE_CHANGE_TYPE, UPDATE_1, TABLE_1, COLUMN_1),
        new Metadata(1L, PARTIAL_UPDATE_CHANGE_TYPE, UPDATE_2, TABLE_1, COLUMN_1),
        new Metadata(1L, PARTIAL_UPDATE_CHANGE_TYPE, UPDATE_2, TABLE_1, COLUMN_2),
        new Metadata(1L, PARTIAL_UPDATE_CHANGE_TYPE, UPDATE_3, TABLE_2, COLUMN_3)
    );
  }

  @Test
  void shouldFindAll() {
    var map = instance.findAll();

    assertThat(map).usingRecursiveFieldByFieldElementComparator(ignoringCollectionOrder)
        .containsExactlyInAnyOrder(
            new PartialUpdate(UPDATE_1, TABLE_1, Set.of(COLUMN_1)),
            new PartialUpdate(UPDATE_2, TABLE_1, Set.of(COLUMN_1, COLUMN_2)),
            new PartialUpdate(UPDATE_3, TABLE_2, Set.of(COLUMN_3))
        );
  }
}
