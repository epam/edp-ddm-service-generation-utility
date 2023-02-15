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

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

public enum SearchConditionPaginationType {
  TRUE("true"),
  FALSE("false"),
  OFFSET("offset"),
  NONE("none"),
  PAGE("page");

  private static final Set<SearchConditionPaginationType> PAGINATION_OFFSET_VALUES = EnumSet.of(TRUE, OFFSET);

  private final String value;

  SearchConditionPaginationType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public static SearchConditionPaginationType findByValue(String value) {
    return Arrays.stream(values())
            .filter(type -> type.getValue().equals(value))
            .findAny()
            .orElse(SearchConditionPaginationType.NONE);
  }

  public boolean isTypeOffset() {
    return PAGINATION_OFFSET_VALUES.contains(this);
  }

  public boolean isTypePage() {
    return PAGE.equals(this);
  }

  public static boolean isTypeOffset(SearchConditionPaginationType type) {
    return PAGINATION_OFFSET_VALUES.contains(type);
  }

  public static boolean isTypePage(SearchConditionPaginationType type) {
    return PAGE.equals(type);
  }
}
