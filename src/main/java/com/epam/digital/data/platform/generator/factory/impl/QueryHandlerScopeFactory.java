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

package com.epam.digital.data.platform.generator.factory.impl;

import static com.epam.digital.data.platform.generator.utils.ReadOperationUtils.getSelectableFields;

import com.epam.digital.data.platform.generator.factory.CrudAbstractScope;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.scope.QueryHandlerScope;
import org.springframework.stereotype.Component;
import schemacrawler.schema.Table;

@Component
public class QueryHandlerScopeFactory extends CrudAbstractScope<QueryHandlerScope> {

  @Override
  protected QueryHandlerScope map(Table table, Context context) {
    QueryHandlerScope scope = new QueryHandlerScope();
    scope.setClassName(getSchemaName(table) + "QueryHandler");
    scope.setSchemaName(getSchemaName(table));
    scope.setPkColumnName(getPkColumn(table).getName());
    scope.setTableName(table.getName());
    scope.setPkType(getPkTypeName(table));
    scope.setOutputFields(getSelectableFields(table.getColumns()));
    return scope;
  }

  @Override
  public String getPath() {
    return "rest-api/src/main/java/restapi/handler/queryHandler.java.ftl";
  }
}
