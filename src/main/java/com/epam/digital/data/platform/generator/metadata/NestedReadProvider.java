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

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

@Component
public class NestedReadProvider {

  static final String NESTED_READ_CHANGE_TYPE = "nestedRead";

  private final MetadataFacade metadataFacade;

  public NestedReadProvider(MetadataFacade metadataFacade) {
    this.metadataFacade = metadataFacade;
  }

  public Map<String, NestedReadEntity> findFor(String tableName) {
    return metadataFacade
        .findByChangeTypeAndChangeName(NESTED_READ_CHANGE_TYPE, tableName)
        .map(metadata -> new NestedReadEntity(tableName, metadata.getValue(), metadata.getName()))
        .collect(toMap(NestedReadEntity::getColumn, Function.identity(), (el1, el2) -> el2));
  }
}
