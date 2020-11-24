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
import static java.util.stream.Collectors.toList;

import java.util.HashSet;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PartialUpdateProvider {

  static final String PARTIAL_UPDATE_CHANGE_TYPE = "partialUpdate";

  private final MetadataFacade metadataFacade;

  public PartialUpdateProvider(MetadataFacade metadataFacade) {
    this.metadataFacade = metadataFacade;
  }

  public List<PartialUpdate> findAll() {
    var mappedByName = metadataFacade
        .findByChangeType(PARTIAL_UPDATE_CHANGE_TYPE)
        .collect(groupingBy(Metadata::getChangeName));

    return mappedByName.values().stream()
        .map(this::toPartialUpdateObj)
        .collect(toList());
  }

  private PartialUpdate toPartialUpdateObj(List<Metadata> list) {
    var updateName = list.get(0).getChangeName();
    var tableName = list.get(0).getName();
    var columns = list.stream().map(Metadata::getValue).collect(toList());
    return new PartialUpdate(updateName, tableName, new HashSet<>(columns));
  }
}
