package com.epam.digital.data.platform.generator.utils;

import com.epam.digital.data.platform.generator.model.template.SelectableField;
import schemacrawler.schema.Table;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class ReadOperationUtils {

  private ReadOperationUtils() {}

  public static List<SelectableField> getSelectableFields(Table table) {
    return table.getColumns().stream()
        .map(
            column ->
                new SelectableField(
                    column.getName(),
                    EntityFieldConverter.getConverterCode(column.getColumnDataType().getName())))
        .collect(toList());
  }
}
