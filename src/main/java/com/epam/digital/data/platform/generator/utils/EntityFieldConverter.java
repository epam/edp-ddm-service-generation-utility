package com.epam.digital.data.platform.generator.utils;

import java.util.Map;

public class EntityFieldConverter {

  private static final String ARRAY = "array";

  private static final Map<String, String> CUSTOM_DB_TYPE_TO_CONVERTER =
      Map.of(
          "typ_file",
          "com.epam.digital.data.platform.kafkaapi.core.util.JooqDataTypes.FILE_DATA_TYPE",
          ARRAY,
          "com.epam.digital.data.platform.kafkaapi.core.util.JooqDataTypes.ARRAY_DATA_TYPE");

  private EntityFieldConverter() {}

  public static String getConverterCode(String dbType) {
    var str = dbType;
    if (str.startsWith("_")) {
      str = ARRAY;
    }
    return CUSTOM_DB_TYPE_TO_CONVERTER.get(str);
  }
}
