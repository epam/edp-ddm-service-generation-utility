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

package com.epam.digital.data.platform.generator.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashSet;
import java.util.Set;

public class TrembitaProcessDefinitions {

  @JsonProperty("process_definitions")
  private Set<ProcessDefinition> processDefinitions = new HashSet<>();

  public Set<ProcessDefinition> getProcessDefinitions() {
    return processDefinitions;
  }

  public void setProcessDefinitions(
      Set<ProcessDefinition> processDefinitions) {
    this.processDefinitions = processDefinitions;
  }

  public void addProcessDefinitionId(String processDefinitionId) {
    var pd = new ProcessDefinition();
    pd.setProcessDefinitionId(processDefinitionId);
    this.processDefinitions.add(pd);
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class ProcessDefinition {

    @JsonProperty("process_definition_id")
    private String processDefinitionId;

    public String getProcessDefinitionId() {
      return processDefinitionId;
    }

    public void setProcessDefinitionId(String processDefinitionId) {
      this.processDefinitionId = processDefinitionId;
    }
  }
}
