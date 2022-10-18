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

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BulkLoadInfoProvider {

  static final String BULK_LOAD_CHANGE_TYPE = "bulkLoad";

  private final MetadataFacade metadataFacade;

  public BulkLoadInfoProvider(MetadataFacade metadataFacade) {
    this.metadataFacade = metadataFacade;
  }

  public Set<String> getTablesWithBulkLoad() {
    return metadataFacade.findByChangeType(BULK_LOAD_CHANGE_TYPE)
            .map(Metadata::getChangeName)
            .collect(Collectors.toUnmodifiableSet());
  }
}
