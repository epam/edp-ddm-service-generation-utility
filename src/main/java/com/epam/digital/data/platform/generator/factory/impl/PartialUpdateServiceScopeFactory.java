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

import static java.util.stream.Collectors.toList;

import com.epam.digital.data.platform.generator.factory.AbstractScope;
import com.epam.digital.data.platform.generator.metadata.PartialUpdateProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.scope.UpdateServiceScope;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PartialUpdateServiceScopeFactory extends AbstractScope<UpdateServiceScope> {

  private final PartialUpdateProvider partialUpdateProvider;

  public PartialUpdateServiceScopeFactory(
      PartialUpdateProvider partialUpdateProvider) {
    this.partialUpdateProvider = partialUpdateProvider;
  }

  @Override
  public List<UpdateServiceScope> create(Context context) {
    return partialUpdateProvider.findAll().stream()
        .map(upd -> {
          var table = findTable(upd.getTableName(), context);

          var scope = new UpdateServiceScope();
          scope.setClassName(getSchemaName(table, upd.getName()) + "UpdateService");
          scope.setSchemaName(getSchemaName(table, upd.getName()));
          scope.setPkName(getPkName(table));
          scope.setPkType(getPkTypeName(table));
          scope.setRequestType(
              "update-" + toHyphenTableName(table) + "-" + toHyphenTableName(upd.getName()));
          return scope;
        })
        .collect(toList());
  }

  @Override
  public String getPath() {
    return "rest-api/src/main/java/restapi/service/update.java.ftl";
  }
}
