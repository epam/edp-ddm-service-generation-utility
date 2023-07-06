/*
 * Copyright 2023 EPAM Systems.
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
import java.util.stream.Collectors;

@Component
public class AsyncDataLoadInfoProvider {

  static final String ASYNC_LOAD_CHANGE_TYPE = "create_async_load";

  private final MetadataFacade metadataFacade;

  public AsyncDataLoadInfoProvider(MetadataFacade metadataFacade) {
    this.metadataFacade = metadataFacade;
  }

  public Map<String, String> getTablesWithAsyncLoad() {
    return metadataFacade.findByChangeType(ASYNC_LOAD_CHANGE_TYPE)
            .map(el ->  Map.entry(el.getName(), el.getValue()))
            .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
  }
}
