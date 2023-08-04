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

package com.epam.digital.data.platform.generator.metadata;

import com.epam.digital.data.platform.generator.model.template.SearchOperatorType;

import java.util.ArrayList;
import java.util.List;

public class SearchConditionOperation {

  private String tableName;
  private List<LogicOperator> logicOperators = new ArrayList<>();

  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public List<LogicOperator> getLogicOperators() {
    return logicOperators;
  }

  public void setLogicOperators(List<LogicOperator> logicOperators) {
    this.logicOperators = logicOperators;
  }

  public static class LogicOperator {
    private SearchOperatorType type;
    private List<String> columns = new ArrayList<>();
    private List<LogicOperator> logicOperators = new ArrayList<>();

    public SearchOperatorType getType() {
      return type;
    }

    public void setType(SearchOperatorType type) {
      this.type = type;
    }

    public List<String> getColumns() {
      return columns;
    }

    public void setColumns(List<String> columns) {
      this.columns = columns;
    }

    public List<LogicOperator> getLogicOperators() {
      return logicOperators;
    }

    public void setLogicOperators(List<LogicOperator> logicOperators) {
      this.logicOperators = logicOperators;
    }
  }
}
