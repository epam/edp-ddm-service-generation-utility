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

import static com.epam.digital.data.platform.generator.utils.ReadOperationUtils.isAsyncSearchCondition;

import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.scope.ListenerScope;
import org.springframework.stereotype.Component;
import schemacrawler.schema.Table;
import com.epam.digital.data.platform.generator.factory.SearchConditionsAbstractScope;

@Component
public class SearchListenerScopeFactory extends SearchConditionsAbstractScope<ListenerScope> {

  @Override
  protected ListenerScope map(Table table, Context context) {
    var scope = new ListenerScope();
    scope.setClassName(getSchemaName(table) + "Listener");
    scope.setSchemaName(getSchemaName(table));

    scope.setOperation("search");
    scope.setRootOfTopicName(toHyphenTableName(table));
    scope.setOutputType(scope.getSchemaName());

    return scope;
  }

  @Override
  protected boolean isApplicable(Table table, Context context) {
    return super.isApplicable(table, context) &&
        isAsyncSearchCondition(table.getName(), context);
  }

  @Override
  public String getPath() {
    return "kafka-api/src/main/java/kafkaapi/listener/search/searchListener.java.ftl";
  }
}
