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

package com.epam.digital.data.platform.generator.factory;

import static java.util.stream.Collectors.groupingBy;

import com.epam.digital.data.platform.generator.utils.PathConverter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class DefaultScopeFactory {

  private final Map<String, ScopeFactory<?>> templateFactory = new HashMap<>();

  public DefaultScopeFactory(List<ScopeFactory<?>> factories) {
    Map<String, List<ScopeFactory<?>>> groupedByPath = factories.stream()
        .collect(groupingBy(this::safePath));

    for (var entry : groupedByPath.entrySet()) {
      if (entry.getValue().size() > 1) {
        templateFactory.put(entry.getKey(), new CompositeScopeFactory(entry.getValue()));
      } else {
        templateFactory.put(entry.getKey(), entry.getValue().get(0));
      }
    }
  }

  private String safePath(ScopeFactory<?> value) {
    return PathConverter.safePath(value.getPath());
  }

  @SuppressWarnings("rawtypes")
  public Scope create(String temp) {
    return templateFactory.get(temp);
  }
}
