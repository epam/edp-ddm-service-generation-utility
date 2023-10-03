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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SoapSearchScope {

  private Set<String> schemaNames = new HashSet<>();
  private List<SearchControllerScope> searchScopes = new ArrayList<>();
  private List<GetFileScopeInfo> fileScopes = new ArrayList<>();

  public Set<String> getSchemaNames() {
    return schemaNames;
  }

  public void setSchemaNames(Set<String> schemaNames) {
    this.schemaNames = schemaNames;
  }

  public List<SearchControllerScope> getSearchScopes() {
    return searchScopes;
  }

  public void setSearchScopes(List<SearchControllerScope> searchScopes) {
    this.searchScopes = searchScopes;
  }

  public List<GetFileScopeInfo> getFileScopes() {
    return fileScopes;
  }

  public void setFileScopes(List<GetFileScopeInfo> fileScopes) {
    this.fileScopes = fileScopes;
  }

  public static class GetFileScopeInfo {

    private String tableEndpoint;
    private String columnEndpoint;
    private String pkType;
    private String methodName;

    public String getTableEndpoint() {
      return tableEndpoint;
    }

    public void setTableEndpoint(String tableEndpoint) {
      this.tableEndpoint = tableEndpoint;
    }

    public String getColumnEndpoint() {
      return columnEndpoint;
    }

    public void setColumnEndpoint(String columnEndpoint) {
      this.columnEndpoint = columnEndpoint;
    }

    public String getPkType() {
      return pkType;
    }

    public void setPkType(String pkType) {
      this.pkType = pkType;
    }

    public String getMethodName() {
      return methodName;
    }

    public void setMethodName(String methodName) {
      this.methodName = methodName;
    }
  }

}
