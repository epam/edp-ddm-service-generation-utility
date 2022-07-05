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

package com.epam.digital.data.platform.generator.utils;

import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.model.template.SelectableField;
import schemacrawler.schema.Column;
import schemacrawler.schema.Table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class ReadOperationUtils {

  public static final String SYNC_READ_TYPE = "sync";
  public static final String ASYNC_READ_TYPE = "async";

  private ReadOperationUtils() {
  }

  public static List<SelectableField> getSelectableFields(
      String tableName, List<Column> columns, Context context) {
    return columns.stream()
        .map(
            column ->
                new SelectableField(
                    column.getName(),
                    tableName,
                    EntityFieldConverter.getConverterCode(
                        column, defineReadType(tableName, context))))
        .collect(toList());
  }

  public static List<SelectableField> getSelectableFields(Table table, List<String> allowedColumns,
      Context context) {
    List<Column> columns = table.getColumns().stream()
        .filter(column -> allowedColumns.contains(column.getName())).collect(toList());
    return getSelectableFields(table.getName(), columns, context);
  }

  public static List<SelectableField> getSelectableFields(Table table, Context context) {
    return getSelectableFields(table.getName(), table.getColumns(), context);
  }

  private static String defineReadType(String tableName, Context context) {
    if (isAsyncTable(tableName, context) || isAsyncSearchCondition(tableName, context)) {
      return ASYNC_READ_TYPE;
    }
    return SYNC_READ_TYPE;
  }

  public static boolean isAsyncTable(String tableName, Context context) {
    return context.getAsyncData().getAsyncTables().contains(tableName);
  }

  public static boolean isAsyncSearchCondition(String tableName, Context context) {
    return context.getAsyncData().getAsyncSearchConditions().contains(tableName);
  }
}
