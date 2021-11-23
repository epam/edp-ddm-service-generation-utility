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

public class ListenerDetails {

  private final String operation;
  private final String rootOfTopicName;
  private final String inputType;
  private final String outputType;

  public ListenerDetails(String operation, String rootOfTopicName, String inputType,
      String outputType) {
    this.operation = operation;
    this.rootOfTopicName = rootOfTopicName;
    this.inputType = inputType;
    this.outputType = outputType;
  }

  public String getOperation() {
    return operation;
  }

  public String getRootOfTopicName() {
    return rootOfTopicName;
  }

  public String getInputType() {
    return inputType;
  }

  public String getOutputType() {
    return outputType;
  }
}
