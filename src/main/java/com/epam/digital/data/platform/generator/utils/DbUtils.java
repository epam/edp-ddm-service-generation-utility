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
import schemacrawler.schema.Column;
import schemacrawler.schema.Table;

import java.util.Collections;
import java.util.List;

public class DbUtils {

  private static final List<String> NON_READABLE_TYPES = Collections.singletonList("geometry");

  private DbUtils() {}

  public static Table findTable(String name, Context context) {
    return context.getCatalog().getTables().stream()
            .filter(t -> t.getName().equals(name))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("Can't find table with name: " + name));
  }

  public static Column findColumn(String name, Table table) {
    return table.getColumns().stream()
            .filter(c -> c.getName().equals(name))
            .findAny()
            .orElseThrow(
                    () ->
                            new IllegalArgumentException(
                                    String.format(
                                            "Can not find in %s the column with name: %s", table.getName(), name)));
  }

  public static boolean isColumnOfArrayType(Column column) {
    return column.getColumnDataType().getName().startsWith("_");
  }

  public static boolean isColumnOfArrayType(String columnName, Table table) {
    return isColumnOfArrayType(findColumn(columnName, table));
  }

  public static boolean isReadableColumn(Column column) {
    return !NON_READABLE_TYPES.contains(column.getColumnDataType().getName());
  }
}
