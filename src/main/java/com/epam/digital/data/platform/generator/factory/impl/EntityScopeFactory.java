package com.epam.digital.data.platform.generator.factory.impl;

import static java.util.stream.Collectors.toList;

import com.epam.digital.data.platform.generator.constraints.impl.CompositeConstraintProvider;
import com.epam.digital.data.platform.generator.factory.AbstractEntityScopeFactory;
import com.epam.digital.data.platform.generator.metadata.EnumProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.model.template.Field;
import com.epam.digital.data.platform.generator.scope.ModelScope;
import com.epam.digital.data.platform.generator.utils.DbTypeConverter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import schemacrawler.schema.Table;

@Component
public class EntityScopeFactory extends AbstractEntityScopeFactory<ModelScope> {

  private final CompositeConstraintProvider constraintProviders;

  public EntityScopeFactory(
      EnumProvider enumProvider,
      CompositeConstraintProvider constraintProviders) {
    super(enumProvider);
    this.constraintProviders = constraintProviders;
  }

  @Override
  public List<ModelScope> create(Context context) {
    var scopes = new ArrayList<ModelScope>();

    scopes.addAll(createScopes(context));

    return scopes;
  }

  private List<ModelScope> createScopes(Context context) {
    return context.getCatalog().getTables().stream()
        .filter(table -> isRecentDataTable(table) || isSearchConditionsView(table))
        .map(t -> {
          var scope = new ModelScope();
          scope.setClassName(getSchemaName(t));
          scope.getFields().addAll(getFields(t));
          return scope;
        })
        .collect(toList());
  }

  private List<Field> getFields(Table table) {
    return table.getColumns().stream()
        .map(column -> {
          var clazzName = DbTypeConverter.convertToJavaTypeName(column);

          var constraints = constraintProviders.getConstraintForProperty(
              column.getColumnDataType().getName(), clazzName);

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
