package com.epam.digital.data.platform.generator.utils;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import schemacrawler.schema.Column;

public class DbTypeConverter {

  private static final HashMap<String, String> BASE_CLASSES = new HashMap<>();
  private static final HashMap<Class<?>, Class<?>> MAPPED_CLASSES = new HashMap<>();

  static {
    BASE_CLASSES.put("uuid", UUID.class.getCanonicalName());
    BASE_CLASSES.put("dn_edrpou", String.class.getCanonicalName());
    BASE_CLASSES.put("dn_passport_num", String.class.getCanonicalName());
    BASE_CLASSES.put("type_file", "com.epam.digital.data.platform.model.core.kafka.File");

    MAPPED_CLASSES.put(Timestamp.class, LocalDateTime.class);
    MAPPED_CLASSES.put(Date.class, LocalDate.class);
    MAPPED_CLASSES.put(Time.class, LocalTime.class);
  }

  private DbTypeConverter() {
  }

  public static String convertToJavaTypeName(Column column) {
    return convertToJavaTypeName(
        column.getColumnDataType().getTypeMappedClass(),
        column.getColumnDataType().getName()
    );
  }

  public static String convertToJavaTypeName(Class<?> clazz, String dbType) {
    return byNameLookup(dbType)
            .orElseGet(() -> mappedClass(clazz).getCanonicalName());
  }

  public static String convertToJavaTypeName(String clazzName, String dbType) {
    return byNameLookup(dbType)
            .orElseGet(() -> {
              try {
                return mappedClass(Class.forName(clazzName)).getCanonicalName();
              } catch (ClassNotFoundException e) {
                throw new IllegalStateException("Attempt to convert to non existing class", e);
              }
            });
  }

  private static Optional<String> byNameLookup(String typeName) {
    return Optional.ofNullable(BASE_CLASSES.get(typeName.toLowerCase()));
  }

  private static Class<?> mappedClass(Class<?> clazz) {
    for (var entry : MAPPED_CLASSES.entrySet()) {
      if (entry.getKey().isAssignableFrom(clazz)) {
        return entry.getValue();
      }
    }
    return clazz;
  }
}
