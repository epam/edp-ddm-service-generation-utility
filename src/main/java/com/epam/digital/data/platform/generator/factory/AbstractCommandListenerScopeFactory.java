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

package com.epam.digital.data.platform.generator.factory;

import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.scope.ListenerScope;
import org.apache.commons.text.CaseUtils;
import schemacrawler.schema.Table;

public abstract class AbstractCommandListenerScopeFactory extends CrudAbstractScope<ListenerScope> {

  protected abstract String getOperation();

  protected abstract String getOutputType();

  @Override
  protected ListenerScope map(Table table, Context context) {
    var scope = new ListenerScope();
    scope.setClassName(
        getSchemaName(table) + CaseUtils.toCamelCase(getOperation(), true, '-') + "Listener");
    scope.setSchemaName(getSchemaName(table));
    scope.setPkType(getPkTypeName(table));

    scope.setOperation(getOperation());
    scope.setRootOfTopicName(toHyphenTableName(table));
    scope.setOutputType(getOutputType());
    scope.setHandlerName(
        getSchemaName(table) + CaseUtils.toCamelCase(getOperation(), true, '-') + "CommandHandler");

    return scope;
  }
}
