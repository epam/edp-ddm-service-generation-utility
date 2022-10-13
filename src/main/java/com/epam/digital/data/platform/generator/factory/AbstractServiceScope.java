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

import com.epam.digital.data.platform.generator.metadata.RlsMetadata;
import com.epam.digital.data.platform.generator.metadata.RlsMetadataFacade;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.scope.ServiceScope;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import schemacrawler.schema.Table;

public abstract class AbstractServiceScope<T extends ServiceScope> extends CrudAbstractScope<T> {

  @Autowired
  private RlsMetadataFacade rlsMetadataFacade;

  protected abstract T instantiate();

  protected abstract String getOperation();

  @Override
  protected T map(Table table, Context context) {
    String modelName = getSchemaName(table);

    T scope = instantiate();

    scope.setClassName(modelName + StringUtils.capitalize(getOperation()) + "Service");
    scope.setSchemaName(modelName);
    scope.setPkName(getPkName(table));
    scope.setPkType(getPkTypeName(table));

    String requestType = getOperation() + "-" + toHyphenTableName(table);
    scope.setRequestType(requestType);
    return scope;
  }

  protected RlsMetadata getRlsMetadata(String tableName) {
    return rlsMetadataFacade.findByTypeAndCheckTable(RlsMetadataFacade.METADATA_TYPE_WRITE, tableName)
            .findFirst().orElse(null);
  }
}
