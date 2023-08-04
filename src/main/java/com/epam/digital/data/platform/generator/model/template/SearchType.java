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

package com.epam.digital.data.platform.generator.model.template;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum SearchType {
  EQUAL("equalColumn"),
  NOT_EQUAL("notEqualColumn"),
  STARTS_WITH("startsWithColumn"),
  STARTS_WITH_ARRAY("startsWithArrayColumn"),
  CONTAINS("containsColumn"),
  IN("inColumn"),
  NOT_IN("notInColumn"),
  BETWEEN("betweenColumn");

  private final String value;

  SearchType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public static SearchType findByValue(String value) {
    return Arrays.stream(values())
            .filter(type -> type.getValue().equals(value))
            .findAny()
            .orElse(null);
  }

  public static List<String> typeValues() {
    return Arrays.stream(SearchType.values())
        .map(SearchType::getValue)
        .collect(Collectors.toList());
  }
}
