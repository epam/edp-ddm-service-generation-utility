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

package com.epam.digital.data.platform.generator.scope;

import java.util.Map;

public class AsyncDataLoadListenerScope extends ListenerScope {

  private String filterName;
  private String csvProcessorName;
  private Map<String, String> entityNamesToSchemaNames;

  public String getCsvProcessorName() {
    return csvProcessorName;
  }

  public void setCsvProcessorName(String csvProcessorName) {
    this.csvProcessorName = csvProcessorName;
  }

  public String getFilterName() {
    return filterName;
  }

  public void setFilterName(String filterName) {
    this.filterName = filterName;
  }

  public Map<String, String> getEntityNamesToSchemaNames() {
    return entityNamesToSchemaNames;
  }

  public void setEntityNamesToSchemaNames(
      Map<String, String> entityNamesToSchemaNames) {
    this.entityNamesToSchemaNames = entityNamesToSchemaNames;
  }
}
