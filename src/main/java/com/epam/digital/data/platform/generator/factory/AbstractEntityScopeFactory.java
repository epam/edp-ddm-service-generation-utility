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

  protected String getArrayOfType(String clazzName) {
    return String.format("%s[]", clazzName);
  }

  protected String getGeneralizedListOfType(String clazzName) {
    return String.format("%s<%s>", List.class.getCanonicalName(), clazzName);
  }
}
