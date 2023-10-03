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

package com.epam.digital.data.platform.generator.utils;

import org.apache.commons.lang3.StringUtils;

public class ProcessDefinitionIdConverter {

  private static final String NON_ALPHANUMERIC_REGEX = "\\P{Alnum}+";
  private ProcessDefinitionIdConverter() {
  }

  public static String convert(String processDefinitionId) {
    var result = new StringBuilder();
    var parts = processDefinitionId.split(NON_ALPHANUMERIC_REGEX);

    for (String part : parts) {
      if (!part.isEmpty()) {
        result.append(StringUtils.capitalize(part));
      }
    }
    return result.toString();
  }
}
