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

import static com.epam.digital.data.platform.generator.metadata.AsyncDataLoadInfoProvider.ASYNC_LOAD_CHANGE_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsyncDataLoadInfoProviderTest {

  private static final String ASYNC_LOAD_TABLE_1 = "table1";
  private static final String ASYNC_LOAD_TABLE_2 = "table2";
  private static final String LIMIT_1 = "100";
  private static final String LIMIT_2 = "1000";

  private AsyncDataLoadInfoProvider asyncDataLoadInfoProvider;

  @Mock
  private MetadataFacade metadataFacade;

  @BeforeEach
  void beforeEach() {
    asyncDataLoadInfoProvider = new AsyncDataLoadInfoProvider(metadataFacade);
  }

  @Test
  void expectAsyncDataLoadTablesMapReturned() {
    when(metadataFacade.findByChangeType(ASYNC_LOAD_CHANGE_TYPE))
            .thenReturn(Stream.of(new Metadata(1L, ASYNC_LOAD_CHANGE_TYPE, "createAsyncLoad", ASYNC_LOAD_TABLE_1, LIMIT_1),
                    new Metadata(2L, ASYNC_LOAD_CHANGE_TYPE, "createAsyncLoad", ASYNC_LOAD_TABLE_2, LIMIT_2)));

    var actual = asyncDataLoadInfoProvider.getTablesWithAsyncLoad();

    assertThat(actual.keySet()).containsExactlyInAnyOrder(ASYNC_LOAD_TABLE_1, ASYNC_LOAD_TABLE_2);
    assertThat(actual.values()).containsExactlyInAnyOrder(LIMIT_1, LIMIT_2);
  }
}
