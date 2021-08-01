package com.epam.digital.data.platform.generator.factory;

import com.epam.digital.data.platform.generator.metadata.EnumProvider;
import com.epam.digital.data.platform.generator.utils.DbTypeConverter;
import java.util.List;
import schemacrawler.schema.Column;

public abstract class AbstractEntityScopeFactory<T> extends AbstractScope<T> {

  private final EnumProvider enumProvider;

  protected AbstractEntityScopeFactory(
      EnumProvider enumProvider) {
    this.enumProvider = enumProvider;
  }

  protected String typeToString(String clazzName, Column column) {
    String type = column.getColumnDataType().getName();

    boolean isArray = type.startsWith("_");
    if (isArray) {
      String rawType = type.substring(1);
      String rawClazzName = DbTypeConverter.convertToJavaTypeName(clazzName, rawType);
      return getGeneralizedListOfType(rawClazzName);
    }

    boolean isEnum = !enumProvider.findFor(type).isEmpty();
    if (isEnum) {
      return getSchemaName(type);
    }

    return clazzName;
  }

  private String getGeneralizedListOfType(String clazzName) {
    return String.format("%s<%s>", List.class.getCanonicalName(), clazzName);
  }
}
