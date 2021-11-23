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

package com.epam.digital.data.platform.generator.model.template;

import java.util.List;

public class EnumEndpoint {

  private String endpoint;
  private String methodName;
  private List<EnumLabel> labels;

  public EnumEndpoint(String endpoint, String methodName,
      List<EnumLabel> labels) {
    this.endpoint = endpoint;
    this.methodName = methodName;
    this.labels = labels;
  }

  public String getEndpoint() {
    return endpoint;
  }

  public void setEndpoint(String endpoint) {
    this.endpoint = endpoint;
  }

  public String getMethodName() {
    return methodName;
  }

  public void setMethodName(String methodName) {
    this.methodName = methodName;
  }

  public List<EnumLabel> getLabels() {
    return labels;
  }

  public void setLabels(
      List<EnumLabel> labels) {
    this.labels = labels;
  }
}
