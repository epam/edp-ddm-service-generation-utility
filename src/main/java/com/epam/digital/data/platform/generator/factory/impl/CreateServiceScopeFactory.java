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

import com.epam.digital.data.platform.generator.model.Context;
import org.springframework.stereotype.Component;
import com.epam.digital.data.platform.generator.factory.AbstractServiceScope;
import com.epam.digital.data.platform.generator.scope.CreateServiceScope;
import schemacrawler.schema.Table;

@Component
public class CreateServiceScopeFactory extends AbstractServiceScope<CreateServiceScope> {

  @Override
  protected CreateServiceScope instantiate() {
    return new CreateServiceScope();
  }

  @Override
  protected CreateServiceScope map(Table table, Context context) {
    var scope = super.map(table, context);
    scope.setRls(getRlsMetadata(table.getName()));
    return scope;
  }

  @Override
  protected String getOperation() {
    return "create";
  }

  @Override
  public String getPath() {
    return "rest-api/src/main/java/restapi/service/create.java.ftl";
  }
}
