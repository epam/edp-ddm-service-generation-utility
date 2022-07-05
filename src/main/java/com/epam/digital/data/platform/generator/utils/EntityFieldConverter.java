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

import schemacrawler.schema.Column;

import java.util.Map;

public class EntityFieldConverter {

  private static final String TYPE_FILE_SYNC = "type_file_sync";
  private static final String ARRAY_SYNC = "array_sync";
  private static final String TYPE_FILE_ARRAY_SYNC = "type_file_array_sync";
  private static final String TYPE_FILE_ASYNC = "type_file_async";
  private static final String ARRAY_ASYNC = "array_async";
  private static final String TYPE_FILE_ARRAY_ASYNC = "type_file_array_async";

  private static final String TYPE_FILE_ARRAY = "type_file_array";
  private static final String ARRAY = "array";
  private static final String UNDERSCORE = "_";

  private static final Map<String, String> CUSTOM_DB_TYPE_TO_CONVERTER =
      Map.of(
          TYPE_FILE_SYNC,
          "com.epam.digital.data.platform.restapi.core.utils.JooqDataTypes.FILE_DATA_TYPE",
          ARRAY_SYNC,
          "com.epam.digital.data.platform.restapi.core.utils.JooqDataTypes.ARRAY_DATA_TYPE",
          TYPE_FILE_ARRAY_SYNC,
          "com.epam.digital.data.platform.restapi.core.utils.JooqDataTypes.FILE_ARRAY_DATA_TYPE",
          TYPE_FILE_ASYNC,
          "com.epam.digital.data.platform.kafkaapi.core.util.JooqDataTypes.FILE_DATA_TYPE",
          ARRAY_ASYNC,
          "com.epam.digital.data.platform.kafkaapi.core.util.JooqDataTypes.ARRAY_DATA_TYPE",
          TYPE_FILE_ARRAY_ASYNC,
          "com.epam.digital.data.platform.restapi.core.utils.JooqDataTypes.FILE_ARRAY_DATA_TYPE"
          );

  private EntityFieldConverter() {
  }

  public static String getConverterCode(Column column, String readType) {
    String type;
    if (DbUtils.isColumnOfArrayType(column)) {
      if (column.getColumnDataType().getName().startsWith("_type_file")) {
        type = TYPE_FILE_ARRAY + UNDERSCORE + readType;
      } else {
        type = ARRAY + UNDERSCORE + readType;
      }
    } else {
      type = column.getColumnDataType().getName() + UNDERSCORE + readType;
    }
    return CUSTOM_DB_TYPE_TO_CONVERTER.get(type);
  }
}
