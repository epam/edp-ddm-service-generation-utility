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

package com.epam.digital.data.platform.generator.scope;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SoapScope {

  private final Set<String> schemaNames = new HashSet<>();
  private final List<SearchControllerScope> searchScopes = new ArrayList<>();

  public Set<String> getSchemaNames() {
    return schemaNames;
  }

  public List<SearchControllerScope> getSearchScopes() {
    return searchScopes;
  }

  public void addSchemaName(String schemaName) {
    schemaNames.add(schemaName);
  }

  public void addSearchScopes(SearchControllerScope searchScope) {
    searchScopes.add(searchScope);
  }
}
