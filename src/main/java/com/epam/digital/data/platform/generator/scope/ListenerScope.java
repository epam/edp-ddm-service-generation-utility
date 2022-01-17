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

public class ListenerScope extends ClassScope {

  private String schemaName;
  private String pkType;
  private String operation;
  private String rootOfTopicName;
  private String outputType;

  public String getSchemaName() {
    return schemaName;
  }

  public void setSchemaName(String schemaName) {
    this.schemaName = schemaName;
  }

  public String getPkType() {
    return pkType;
  }

  public void setPkType(String pkType) {
    this.pkType = pkType;
  }

  public String getOperation() {
    return operation;
  }

  public void setOperation(String operation) {
    this.operation = operation;
  }

  public String getRootOfTopicName() {
    return rootOfTopicName;
  }

  public void setRootOfTopicName(String rootOfTopicName) {
    this.rootOfTopicName = rootOfTopicName;
  }

  public String getOutputType() {
    return outputType;
  }

  public void setOutputType(String outputType) {
    this.outputType = outputType;
  }
}
