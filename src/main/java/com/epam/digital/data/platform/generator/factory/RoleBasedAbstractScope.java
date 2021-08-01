package com.epam.digital.data.platform.generator.factory;

import static java.util.stream.Collectors.toList;

import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.permissionmap.PermissionMap;
import java.util.Collection;
import java.util.List;
import schemacrawler.schema.Table;

public abstract class RoleBasedAbstractScope<T> extends AbstractScope<T> {

  protected final PermissionMap permissionMap;

  protected RoleBasedAbstractScope(
      PermissionMap permissionMap) {
    this.permissionMap = permissionMap;
  }

  protected abstract boolean isApplicable(Table table);

  protected abstract List<T> map(Table table, Context context);

  @Override
  public List<T> create(Context context) {
    return context.getCatalog().getTables().stream()
        .filter(this::isRecentDataTable)
        .filter(this::isApplicable)
        .map(t -> map(t, context))
        .flatMap(Collection::stream)
        .collect(toList());
  }
}
