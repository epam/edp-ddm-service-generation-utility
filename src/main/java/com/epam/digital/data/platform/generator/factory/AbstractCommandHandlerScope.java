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
import com.epam.digital.data.platform.generator.scope.CommandHandlerScope;
import org.apache.commons.lang3.StringUtils;
import schemacrawler.schema.Table;

public abstract class AbstractCommandHandlerScope extends CrudAbstractScope<CommandHandlerScope> {

  protected abstract String getOperation();

  @Override
  protected CommandHandlerScope map(Table table, Context context) {
    String modelName = getSchemaName(table);

    var scope = new CommandHandlerScope();

    scope.setClassName(modelName + StringUtils.capitalize(getOperation()) + "CommandHandler");
    scope.setSchemaName(modelName + "Model");
    scope.setTableDataProviderName(modelName + "TableDataProvider");
    scope.setOperation(getOperation());
    return scope;
  }
}
