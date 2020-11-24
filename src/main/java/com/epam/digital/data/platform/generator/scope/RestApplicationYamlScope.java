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

import java.util.List;
import java.util.Map;

public class RestApplicationYamlScope extends ApplicationYamlScope {

  private Map<String, List<String>> entityPaths;
  private List<String> searchPaths;
  private List<String> nestedPaths;
  private boolean isEnumPresent;
  private Integer retentionPolicyDaysRead;
  private Integer retentionPolicyDaysWrite;

  public Map<String, List<String>> getEntityPaths() {
    return entityPaths;
  }

  public void setEntityPaths(Map<String, List<String>> entityPaths) {
    this.entityPaths = entityPaths;
  }

  public List<String> getSearchPaths() {
    return searchPaths;
  }

  public void setSearchPaths(List<String> searchPaths) {
    this.searchPaths = searchPaths;
  }

  public List<String> getNestedPaths() {
    return nestedPaths;
  }

  public void setNestedPaths(List<String> nestedPaths) {
    this.nestedPaths = nestedPaths;
  }

  public boolean isEnumPresent() {
    return isEnumPresent;
  }

  public void setEnumPresent(boolean enumPresent) {
    isEnumPresent = enumPresent;
  }

  public Integer getRetentionPolicyDaysRead() {
    return retentionPolicyDaysRead;
  }

  public void setRetentionPolicyDaysRead(Integer retentionPolicyDaysRead) {
    this.retentionPolicyDaysRead = retentionPolicyDaysRead;
  }

  public Integer getRetentionPolicyDaysWrite() {
    return retentionPolicyDaysWrite;
  }

  public void setRetentionPolicyDaysWrite(Integer retentionPolicyDaysWrite) {
    this.retentionPolicyDaysWrite = retentionPolicyDaysWrite;
  }
}
