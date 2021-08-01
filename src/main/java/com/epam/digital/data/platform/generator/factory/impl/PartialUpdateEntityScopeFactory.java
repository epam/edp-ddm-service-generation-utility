package com.epam.digital.data.platform.generator.factory.impl;

import static com.epam.digital.data.platform.generator.utils.CaseConverter.camelToUnderscore;
import static java.util.stream.Collectors.toList;

import com.epam.digital.data.platform.generator.constraints.impl.CompositeConstraintProvider;
import com.epam.digital.data.platform.generator.factory.AbstractEntityScopeFactory;
import com.epam.digital.data.platform.generator.metadata.EnumProvider;
import com.epam.digital.data.platform.generator.metadata.PartialUpdateProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.model.template.Field;
import com.epam.digital.data.platform.generator.scope.ModelScope;
import com.epam.digital.data.platform.generator.utils.DbTypeConverter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;
import schemacrawler.schema.Table;

@Component
public class PartialUpdateEntityScopeFactory extends AbstractEntityScopeFactory<ModelScope> {

  private final PartialUpdateProvider partialUpdateProvider;
  private final CompositeConstraintProvider constraintProviders;

  public PartialUpdateEntityScopeFactory(
      PartialUpdateProvider partialUpdateProvider,
      EnumProvider enumProvider,
      CompositeConstraintProvider constraintProviders) {
    super(enumProvider);
    this.partialUpdateProvider = partialUpdateProvider;
    this.constraintProviders = constraintProviders;
  }

  @Override
  public List<ModelScope> create(Context context) {
    return partialUpdateProvider.findAll().stream()
        .map(upd -> {
          var table = findTable(upd.getTableName(), context);

          var columns = new HashSet<>(upd.getColumns());
          columns.add(camelToUnderscore(getPkName(table)));

          var scope = new ModelScope();
          scope.setClassName(getSchemaName(table, upd.getName()));
          scope.getFields().addAll(getFields(table, columns));
          return scope;
        })
        .collect(toList());
  }

  private List<Field> getFields(Table table, Set<String> fields) {
    return fields.stream()
        .map(f -> {
          var column = findColumn(f, table);

          var clazzName = DbTypeConverter.convertToJavaTypeName(column);

          var constraints = constraintProviders.getConstraintForProperty(
              column.getColumnDataType().getName(), clazzName);

          var field = new Field();
          field.setName(getPropertyName(f));
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
