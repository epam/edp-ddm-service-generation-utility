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
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;
import org.apache.commons.text.CaseUtils;
import schemacrawler.schema.Column;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableConstraint;

import static com.epam.digital.data.platform.generator.utils.DbTypeConverter.convertToJavaTypeName;

public abstract class AbstractScope<T> implements ScopeFactory<T> {

  protected static final String HISTORICAL_DATA_TABLE_NAME_SUFFIX = "_hst";
  protected static final String M2M_VIEW_NAME_SUFFIX = "_rel_v";
  protected static final String SEARCH_CONDITIONS_VIEW_NAME_SUFFIX = "_v";

  protected Column getPkColumn(Table table) {
    return Optional.ofNullable(table.getPrimaryKey())
        .map(TableConstraint::getColumns)
        .map(Collection::stream)
        .flatMap(Stream::findFirst)
        .orElseThrow(
            () -> new IllegalStateException(
                "No primary key found to create parameter: " + table.getName()));
  }

  protected String getPkTypeName(Table table) {
    return convertToJavaTypeName(getPkColumn(table));
  }

  protected String getPkName(Table table) {
    return getPropertyName(getPkColumn(table));
  }

  protected String getEndpoint(String tableName) {
    return "/" + toHyphenTableName(tableName);
  }

  protected String toHyphenTableName(Table table) {
    return toHyphenTableName(table.getName());
  }

  protected String toHyphenTableName(String tableName) {
    return getCutTableName(tableName).replace("_", "-");
  }

  protected String getCutTableName(Table table) {
    return getCutTableName(table.getName());
  }

  protected String getCutTableName(String tableName) {
    var t = tableName;

    if (t.endsWith(HISTORICAL_DATA_TABLE_NAME_SUFFIX)) {
      t = t.substring(0, t.length() - HISTORICAL_DATA_TABLE_NAME_SUFFIX.length());
    } else if (t.endsWith(M2M_VIEW_NAME_SUFFIX)) {
      t = t.substring(0, t.length() - M2M_VIEW_NAME_SUFFIX.length());
    } else if (t.endsWith(SEARCH_CONDITIONS_VIEW_NAME_SUFFIX)) {
      t = t.substring(0, t.length() - SEARCH_CONDITIONS_VIEW_NAME_SUFFIX.length());
    }

    return t;
  }

  protected String getSchemaName(Table table) {
    return getSchemaName(table.getName());
  }

  protected String getSchemaName(String tableName) {
    return CaseUtils.toCamelCase(getCutTableName(tableName), true, '-', '_');
  }

  protected String getSchemaName(Table table, String ... appendix) {
    var sb = new StringBuilder(getSchemaName(table));
    for (String s: appendix) {
      sb.append(getSchemaName(s));
    }
    return sb.toString();
  }

  protected String getPropertyName(Column column) {
    return getPropertyName(column.getName());
  }

  protected String getPropertyName(String columnName) {
    return CaseUtils.toCamelCase(columnName, false, '-', '_');
  }

  protected Table findTable(String name, Context context) {
    return context.getCatalog().getTables().stream()
        .filter(t -> t.getName().equals(name))
        .findAny()
        .orElseThrow(() -> new IllegalArgumentException("Can't find table with name: " + name));
  }

  protected Column findColumn(String name, Table table) {
    return table.getColumns().stream()
        .filter(c -> c.getName().equals(name))
        .findAny()
        .orElseThrow(() -> new IllegalArgumentException(String
            .format("Can not find in %s the column with name: %s", table.getName(), name)));
  }

  protected boolean isHistoricalDataTable(Table table) {
    return !table.getTableType().isView()
        && table.getName().endsWith(HISTORICAL_DATA_TABLE_NAME_SUFFIX);
  }

  protected boolean isRecentDataTable(Table table) {
    return !table.getTableType().isView()
        && !table.getName().endsWith(HISTORICAL_DATA_TABLE_NAME_SUFFIX);
  }

  protected boolean isM2MView(Table table) {
    return table.getTableType().isView()
        && table.getName().endsWith(M2M_VIEW_NAME_SUFFIX);
  }

  protected boolean isSearchConditionsView(Table table) {
    return !isM2MView(table)
        && table.getTableType().isView()
        && table.getName().endsWith(SEARCH_CONDITIONS_VIEW_NAME_SUFFIX);
  }
}
