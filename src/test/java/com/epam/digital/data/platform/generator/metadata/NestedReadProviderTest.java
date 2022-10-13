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

import static com.epam.digital.data.platform.generator.metadata.NestedReadProvider.NESTED_READ_CHANGE_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NestedReadProviderTest {

  private static final String MAIN_TABLE_NAME = "table";
  private static final String RELATED_TABLE_NAME = "related_table";
  private static final String COLUMN_NAME = "column";

  private NestedReadProvider nestedReadProvider;

  @Mock
  private MetadataFacade metadataFacade;

  @Mock
  private RlsMetadataFacade rlsMetadataFacade;

  @BeforeEach
  void beforeEach() {
    nestedReadProvider = new NestedReadProvider(metadataFacade, rlsMetadataFacade);
  }

  @Test
  void expectValidNestedEntitiesMapForTableAreCreated() {
    when(metadataFacade.findByChangeTypeAndChangeName(NESTED_READ_CHANGE_TYPE, MAIN_TABLE_NAME))
        .thenReturn(
            Stream.of(
                new Metadata(
                    1L, NESTED_READ_CHANGE_TYPE, MAIN_TABLE_NAME, RELATED_TABLE_NAME, COLUMN_NAME)));

    var actual = nestedReadProvider.findFor(MAIN_TABLE_NAME);

    var expectedNestedReadEntity =
        new NestedReadEntity(MAIN_TABLE_NAME, COLUMN_NAME, RELATED_TABLE_NAME);
    assertThat(actual).containsOnlyKeys(COLUMN_NAME);
    assertThat(actual.get(COLUMN_NAME))
        .usingRecursiveComparison()
        .isEqualTo(expectedNestedReadEntity);
  }
}