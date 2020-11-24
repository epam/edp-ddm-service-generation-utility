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

import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.utils.ContextTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static com.epam.digital.data.platform.generator.metadata.NestedStructureProvider.NESTED_CHANGE_TYPE;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.*;
import static com.epam.digital.data.platform.generator.utils.NestedStructureUtils.mockMultipleChildNodesNestedStructure;
import static com.epam.digital.data.platform.generator.utils.NestedStructureUtils.mockNestedDbCatalog;
import static com.epam.digital.data.platform.generator.utils.NestedStructureUtils.mockSingleChildChainNestedStructure;
import static com.epam.digital.data.platform.generator.utils.NestedStructureUtils.mockSingleLevelOfDepthNestedStructure;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NestedStructureProviderTest {

  private static final String NESTED_STRUCTURE_NAME = "nesting_flow";

  private NestedStructureProvider nestedStructureProvider;

  @Mock
  private MetadataFacade metadataFacade;

  private final Context context = new Context(getSettings(), mockNestedDbCatalog(),
      ContextTestUtils.emptyAsyncData());

  @BeforeEach
  void beforeEach() {
    nestedStructureProvider = new NestedStructureProvider(metadataFacade, context);
  }

  @Test
  void expectSingleChildChainIsBuilt() {
    when(metadataFacade.findByChangeType(NESTED_CHANGE_TYPE))
        .thenReturn(
            Stream.of(
                mockMetadata("application", "order_id"), mockMetadata("order", "transaction_id")));

    nestedStructureProvider.buildNestedStructures();
    var actualNestedStructure = nestedStructureProvider.findAll().get(0);

    var expectedNestedStructure = mockSingleChildChainNestedStructure();
    assertThat(actualNestedStructure).usingRecursiveComparison().isEqualTo(expectedNestedStructure);
  }

  @Test
  void expectStructureWithMultipleChildNodesIsBuilt() {
    when(metadataFacade.findByChangeType(NESTED_CHANGE_TYPE))
        .thenReturn(
            Stream.of(
                mockMetadata("application", "order_id"),
                mockMetadata("order", "transaction_id"),
                mockMetadata("order", "item_id")));

    nestedStructureProvider.buildNestedStructures();
    var actualNestedStructure = nestedStructureProvider.findAll().get(0);

    var expectedNestedStructure = mockMultipleChildNodesNestedStructure();
    assertThat(actualNestedStructure).usingRecursiveComparison().isEqualTo(expectedNestedStructure);
  }

  @Test
  void expectStructureWithOneLevelOfNestingIsBuilt() {
    when(metadataFacade.findByChangeType(NESTED_CHANGE_TYPE))
        .thenReturn(Stream.of(mockMetadata("application", "order_id")));

    nestedStructureProvider.buildNestedStructures();
    var actualNestedStructure = nestedStructureProvider.findAll().get(0);

    var expectedNestedStructure = mockSingleLevelOfDepthNestedStructure();
    assertThat(actualNestedStructure).usingRecursiveComparison().isEqualTo(expectedNestedStructure);
  }

  @Test
  void expectNoStructuresAreBuiltIfNoMetadata() {
    when(metadataFacade.findByChangeType(NESTED_CHANGE_TYPE)).thenReturn(Stream.empty());

    nestedStructureProvider.buildNestedStructures();
    var actualNestedStructures = nestedStructureProvider.findAll();

    assertThat(actualNestedStructures).isEmpty();
  }

  @Test
  void expectExceptionIfCannotFindRoot() {
    when(metadataFacade.findByChangeType(NESTED_CHANGE_TYPE))
        .thenReturn(
            Stream.of(
                mockMetadata("application", "order_id"), mockMetadata("transaction", "item_id")));

    assertThrows(
        IllegalStateException.class, () -> nestedStructureProvider.buildNestedStructures());
  }

  @Test
  void expectExceptionIfCannotFindNestedReferencedColumn() {
    when(metadataFacade.findByChangeType(NESTED_CHANGE_TYPE))
        .thenReturn(Stream.of(mockMetadata("application", "name")));

    assertThrows(
        IllegalArgumentException.class, () -> nestedStructureProvider.buildNestedStructures());
  }

  private Metadata mockMetadata(String nestingTableName, String nestingColumnName) {
    return new Metadata(
        1L, NESTED_CHANGE_TYPE, NESTED_STRUCTURE_NAME, nestingTableName, nestingColumnName);
  }
}
