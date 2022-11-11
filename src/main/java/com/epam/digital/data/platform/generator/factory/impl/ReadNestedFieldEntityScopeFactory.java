/*
 * Copyright 2022 EPAM Systems.
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

package com.epam.digital.data.platform.generator.factory.impl;

import com.epam.digital.data.platform.generator.constraints.impl.CompositeConstraintProvider;
import com.epam.digital.data.platform.generator.factory.AbstractEntityScopeFactory;
import com.epam.digital.data.platform.generator.metadata.EnumProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.model.template.Field;
import com.epam.digital.data.platform.generator.scope.ModelScope;
import com.epam.digital.data.platform.generator.utils.DbTypeConverter;
import com.epam.digital.data.platform.generator.utils.DbUtils;
import org.springframework.stereotype.Component;
import schemacrawler.schema.Column;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class ReadNestedFieldEntityScopeFactory extends AbstractEntityScopeFactory<ModelScope> {

  private final CompositeConstraintProvider constraintProviders;

  public ReadNestedFieldEntityScopeFactory(
      EnumProvider enumProvider, CompositeConstraintProvider constraintProviders) {
    super(enumProvider);
    this.constraintProviders = constraintProviders;
  }

  @Override
  public List<ModelScope> create(Context context) {
    return context.getCatalog().getTables().stream()
        .filter(this::isRecentDataTable)
        .map(
            t -> {
              var scope = new ModelScope();
              scope.setClassName(getSchemaName(t) + "ReadNested");
              scope.getFields().addAll(getFields(t.getColumns()));
              return scope;
            })
        .collect(toList());
  }

  private List<Field> getFields(List<Column> columns) {
    return columns.stream()
            .filter(DbUtils::isReadableColumn)
            .map(
            column -> {
              var clazzName = DbTypeConverter.convertToJavaTypeName(column);

              var constraints =
                  constraintProviders.getConstraintForProperty(column, clazzName);

              var field = new Field();
              field.setName(getPropertyName(column));
              field.setType(typeToString(clazzName, column));
              field.setConstraints(constraints);
              return field;
            })
        .collect(toList());
  }

  @Override
  public String getPath() {
    return "model/src/main/java/model/dto/dto.java.ftl";
  }
}
