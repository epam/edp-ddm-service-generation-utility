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

import com.epam.digital.data.platform.generator.model.AsyncData;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class AsyncDataProvider {

  public static final String READ_MODE_CHANGE_TYPE = "readMode";
  public static final String CREATE_TABLE_CHANGE_NAME = "createTable";
  public static final String SEARCH_CONDITIONS_VIEW_NAME_SUFFIX = "_v";
  private final MetadataFacade metadataFacade;

  public AsyncDataProvider(MetadataFacade metadataFacade) {
    this.metadataFacade = metadataFacade;
  }

  public AsyncData findAll() {
    Set<String> asyncTables = new HashSet<>();
    Set<String> asyncSearchConditions = new HashSet<>();

    var readModeMetadata = metadataFacade
        .findByChangeType(READ_MODE_CHANGE_TYPE)
        .collect(Collectors.toList());
    for (Metadata metadata : readModeMetadata) {
      if (metadata.getChangeName().equals(CREATE_TABLE_CHANGE_NAME)) {
        asyncTables.add(metadata.getName());
      } else {
        asyncSearchConditions.add(metadata.getName().concat(SEARCH_CONDITIONS_VIEW_NAME_SUFFIX));
      }
    }
    return new AsyncData(asyncTables, asyncSearchConditions);
  }
}
