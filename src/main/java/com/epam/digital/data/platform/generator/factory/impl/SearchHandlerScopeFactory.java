package com.epam.digital.data.platform.generator.factory.impl;

import static com.epam.digital.data.platform.generator.utils.ReadOperationUtils.getSelectableFields;
import static java.util.stream.Collectors.toList;

import com.epam.digital.data.platform.generator.metadata.EnumProvider;
import com.epam.digital.data.platform.generator.metadata.SearchConditionProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.model.template.SearchConditionField;
import com.epam.digital.data.platform.generator.scope.SearchHandlerScope;
import com.epam.digital.data.platform.generator.utils.DbTypeConverter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.Table;
import com.epam.digital.data.platform.generator.factory.SearchConditionsAbstractScope;

@Component
public class SearchHandlerScopeFactory extends SearchConditionsAbstractScope<SearchHandlerScope> {

  static final String OP_CONTAINS_IGNORE_CASE = "containsIgnoreCase";
  static final String OP_STARTS_WITH_IGNORE_CASE = "startsWithIgnoreCase";
  static final String OP_EQUAL_IGNORE_CASE = "equalIgnoreCase";
  static final String OP_EQ = "eq";

  private final SearchConditionProvider searchConditionProvider;
  private final EnumProvider enumProvider;

  public SearchHandlerScopeFactory(
      SearchConditionProvider searchConditionProvider, EnumProvider enumProvider) {
    this.searchConditionProvider = searchConditionProvider;
    this.enumProvider = enumProvider;
  }

  @Override
  protected SearchHandlerScope map(Table table, Context context) {
    var sc = searchConditionProvider.findFor(getCutTableName(table));

    var searchConditionFields = new ArrayList<SearchConditionField>();

    searchConditionFields.addAll(sc.getEqual().stream()
        .map(x -> new SearchConditionField(getPropertyName(x), x, chooseEqOpFor(x, table)))
        .collect(toList())
    );

    searchConditionFields.addAll(sc.getContains().stream()
        .map(x -> new SearchConditionField(getPropertyName(x), x, OP_CONTAINS_IGNORE_CASE))
        .collect(toList())
    );

    searchConditionFields.addAll(sc.getStartsWith().stream()
        .map(x -> new SearchConditionField(getPropertyName(x), x, OP_STARTS_WITH_IGNORE_CASE))
        .collect(toList())
    );

    Set<String> allEnums = enumProvider.findAllWithValues().keySet();
    List<String> enumSearchConditionFields = table.getColumns().stream()
        .filter(col -> allEnums.contains(col.getColumnDataType().getName()))
        .map(NamedObject::getName)
        .collect(toList());

    var scope = new SearchHandlerScope();
    scope.setClassName(getSchemaName(table) + "SearchHandler");
    scope.setSchemaName(getSchemaName(table));
    scope.setTableName(table.getName());
    scope.setLimit(sc.getLimit());
    scope.setSearchConditionFields(searchConditionFields);
    scope.setEnumSearchConditionFields(enumSearchConditionFields);
    scope.setOutputFields(getSelectableFields(table));
    scope.setPagination(sc.getPagination());
    return scope;
  }

  private String chooseEqOpFor(String columnName, Table table) {
    var column = table.getColumns().stream()
        .filter(x -> x.getName().equals(columnName))
        .findFirst()
        .orElseThrow(() -> new IllegalStateException(
            String.format(
                "Can not find in %s the column with name: %s",
                table.getName(),
                columnName)));

    String s = OP_EQ;

    String typeName = column.getColumnDataType().getName();
    boolean isEnum = !enumProvider.findFor(typeName).isEmpty();
    if (isEnum) {
      return s;
    }

    var type = DbTypeConverter.convertToJavaTypeName(column);
    if (String.class.getCanonicalName().equals(type)) {
      s = OP_EQUAL_IGNORE_CASE;
    }

    return s;
  }

  @Override
  public String getPath() {
    return "rest-api/src/main/java/restapi/handler/searchHandler.java.ftl";
  }
}
