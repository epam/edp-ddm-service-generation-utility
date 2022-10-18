/*
 * Copyright 2022 EPAM Systems.
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static com.epam.digital.data.platform.generator.metadata.BulkLoadInfoProvider.BULK_LOAD_CHANGE_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BulkLoadInfoProviderTest {

  private static final String BULK_LOAD_TABLE_1 = "table1";
  private static final String BULK_LOAD_TABLE_2 = "table2";

  private BulkLoadInfoProvider bulkLoadInfoProvider;

  @Mock
  private MetadataFacade metadataFacade;

  @BeforeEach
  void beforeEach() {
    bulkLoadInfoProvider = new BulkLoadInfoProvider(metadataFacade);
  }

  @Test
  void expectBulkLoadTablesSetReturned() {
    when(metadataFacade.findByChangeType(BULK_LOAD_CHANGE_TYPE))
            .thenReturn(Stream.of(new Metadata(1L, BULK_LOAD_CHANGE_TYPE, BULK_LOAD_TABLE_1, BULK_LOAD_CHANGE_TYPE, "true"),
                    new Metadata(2L, BULK_LOAD_CHANGE_TYPE, BULK_LOAD_TABLE_2, BULK_LOAD_CHANGE_TYPE, "true")));

    var actual = bulkLoadInfoProvider.getTablesWithBulkLoad();

    assertThat(actual).containsExactlyInAnyOrder(BULK_LOAD_TABLE_1, BULK_LOAD_TABLE_2);
  }
}
