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

import com.epam.digital.data.platform.generator.model.template.RlsFieldRestriction;

public abstract class ServiceScope extends ClassScope {

  private String schemaName;
  private String requestType;
  private String pkName;
  private String pkType;

  private RlsFieldRestriction rls;

  public String getSchemaName() {
    return schemaName;
  }

  public void setSchemaName(String schemaName) {
    this.schemaName = schemaName;
  }

  public String getRequestType() {
    return requestType;
  }

  public void setRequestType(String requestType) {
    this.requestType = requestType;
  }

  public String getPkName() {
    return pkName;
  }

  public void setPkName(String pkName) {
    this.pkName = pkName;
  }

  public String getPkType() {
    return pkType;
  }

  public void setPkType(String pkType) {
    this.pkType = pkType;
  }

  public RlsFieldRestriction getRls() {
    return rls;
  }

  public void setRls(RlsFieldRestriction rls) {
    this.rls = rls;
  }
}
