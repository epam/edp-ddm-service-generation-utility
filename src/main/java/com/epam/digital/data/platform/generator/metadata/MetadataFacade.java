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

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableMap;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toUnmodifiableList;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;

@Component
public class MetadataFacade {

  private final Map<String, List<Metadata>> rows;

  public MetadataFacade(MetadataRepository repository) {
    var map = repository.findAll().stream()
        .collect(groupingBy(Metadata::getChangeType, mapping(identity(), toUnmodifiableList())));

    this.rows = unmodifiableMap(map);
  }

  public Stream<Metadata> findByChangeType(String changeType) {
    return getOrEmpty(changeType).stream();
  }

  public Stream<Metadata> findByChangeTypeAndChangeName(String changeType, String changeName) {
    return filter(changeType, x -> x.getChangeName().equals(changeName));
  }

  public Stream<Metadata> findByChangeTypeAndName(String changeType, String name) {
    return filter(changeType, x -> x.getName().equals(name));
  }

  public Stream<Metadata> findByChangeTypeAndChangeNameAndName(String changeType, String changeName,
      String name) {
    return filter(changeType,
        x -> x.getChangeName().equals(changeName) && x.getName().equals(name));
  }

  private Stream<Metadata> filter(String changeType, Predicate<Metadata> p) {
    return getOrEmpty(changeType).stream().filter(p);
  }

  private List<Metadata> getOrEmpty(String changeType) {
    return rows.getOrDefault(changeType, emptyList());
  }

  public Stream<Metadata> findByChangeTypeAndValueIn(String changeType, List<String> values) {
    return filter(changeType,
        x -> values.contains(x.getValue()));
  }
}
