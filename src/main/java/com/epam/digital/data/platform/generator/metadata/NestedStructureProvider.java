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
import com.epam.digital.data.platform.generator.utils.DbUtils;
import com.google.common.collect.Sets;
import org.springframework.stereotype.Component;
import schemacrawler.schema.Column;
import schemacrawler.schema.Table;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

@Component
public class NestedStructureProvider {

  static final String NESTED_CHANGE_TYPE = "nested";

  private final MetadataFacade metadataFacade;
  private final Context context;

  private List<NestedStructure> nestedStructures;

  public NestedStructureProvider(MetadataFacade metadataFacade, Context context) {
    this.metadataFacade = metadataFacade;
    this.context = context;
  }

  @PostConstruct
  void buildNestedStructures() {
    nestedStructures = Collections.unmodifiableList(createAllNestedStructures());
  }

  public List<NestedStructure> findAll() {
    return nestedStructures;
  }

  private List<NestedStructure> createAllNestedStructures() {
    return findAllNestedMetadataGroupedByName().entrySet().stream()
        .map(entry -> createNestedStructure(entry.getKey(), entry.getValue()))
        .collect(toList());
  }

  private Map<String, List<Metadata>> findAllNestedMetadataGroupedByName() {
    return metadataFacade
        .findByChangeType(NESTED_CHANGE_TYPE)
        .collect(groupingBy(Metadata::getChangeName));
  }

  private NestedStructure createNestedStructure(
          String structureName, List<Metadata> structureMetadata) {
    var nestedStructure = new NestedStructure();
    nestedStructure.setName(structureName);

    var entryTableName = getNestingEntryPointTable(structureMetadata);

    var rootEntry = new NestedNode();
    rootEntry.setTableName(entryTableName);
    rootEntry.setChildNodes(getNested(structureMetadata, rootEntry));
    nestedStructure.setRoot(rootEntry);
    return nestedStructure;
  }

  private Map<String, NestedNode> getNested(
      List<Metadata> metadata, NestedNode curr) {
    var childrenMetadata =
        metadata.stream().filter(m -> m.getName().equals(curr.getTableName())).collect(toList());
    if (childrenMetadata.isEmpty()) {
      return Collections.emptyMap();
    }
    return childrenMetadata.stream()
        .map(
            metadataEntry -> {
              var parentColumnName = metadataEntry.getValue();
              var nestedElement = new NestedNode();
              var childColumn =
                  getNestingReferencedColumn(
                      metadataEntry.getName(), metadataEntry.getValue());
              nestedElement.setTableName(childColumn.getParent().getName());
              nestedElement.setChildNodes(getNested(metadata, nestedElement));
              return Map.entry(parentColumnName, nestedElement);
            })
        .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  private String getNestingEntryPointTable(List<Metadata> metadata) {
    var allTablesWithNesting = metadata.stream()
            .map(Metadata::getName)
            .collect(toSet());
    var referencedTables = metadata.stream()
            .map(m -> getNestingReferencedColumn(m.getName(), m.getValue()))
            .map(Column::getParent)
            .map(Table::getName)
            .collect(toSet());
    var nonReferencedTables = Sets.difference(allTablesWithNesting, referencedTables);
    if (nonReferencedTables.size() != 1) {
      throw new IllegalStateException(
          "Couldn't find entry point for nesting structure, possible options: "
              + nonReferencedTables);
    }
    return nonReferencedTables.iterator().next();
  }

  private Column getNestingReferencedColumn(
      String parentTableName, String parentColumnName) {
    var table = DbUtils.findTable(parentTableName, context);
    var column = DbUtils.findColumn(parentColumnName, table);
    return Optional.ofNullable(column.getReferencedColumn())
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    String.format(
                        "No referenced columns exists for column %s in table %s",
                        parentColumnName, parentTableName)));
  }
}
