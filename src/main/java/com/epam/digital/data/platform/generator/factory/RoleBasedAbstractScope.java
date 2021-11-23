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

import static java.util.stream.Collectors.toList;

import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.permissionmap.PermissionMap;
import java.util.Collection;
import java.util.List;
import schemacrawler.schema.Table;

public abstract class RoleBasedAbstractScope<T> extends AbstractScope<T> {

  protected final PermissionMap permissionMap;

  protected RoleBasedAbstractScope(
      PermissionMap permissionMap) {
    this.permissionMap = permissionMap;
  }

  protected abstract boolean isApplicable(Table table);

  protected abstract List<T> map(Table table, Context context);

  @Override
  public List<T> create(Context context) {
    return context.getCatalog().getTables().stream()
        .filter(this::isRecentDataTable)
        .filter(this::isApplicable)
        .map(t -> map(t, context))
        .flatMap(Collection::stream)
        .collect(toList());
  }
}
