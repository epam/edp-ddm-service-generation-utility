package com.epam.digital.data.platform.generator.factory.impl;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import com.epam.digital.data.platform.generator.factory.AbstractScope;
import com.epam.digital.data.platform.generator.metadata.PartialUpdate;
import com.epam.digital.data.platform.generator.metadata.PartialUpdateProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.permissionmap.PermissionMap;
import com.epam.digital.data.platform.generator.scope.ControllerScope;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import org.springframework.stereotype.Component;
import schemacrawler.schema.Table;

@Component
public class PartialUpdateControllerScopeFactory extends AbstractScope<ControllerScope> {

  private final PartialUpdateProvider partialUpdateProvider;
  private final PermissionMap permissionMap;

  public PartialUpdateControllerScopeFactory(
      PartialUpdateProvider partialUpdateProvider,
      PermissionMap permissionMap) {
    this.partialUpdateProvider = partialUpdateProvider;
    this.permissionMap = permissionMap;
  }

  @Override
  public List<ControllerScope> create(Context context) {
    return partialUpdateProvider.findAll().stream()
        .map(upd -> {
          var table = findTable(upd.getTableName(), context);

          var scope = new ControllerScope();
          scope.setClassName(getSchemaName(table, upd.getName()) + "Controller");
          scope.setSchemaName(getSchemaName(table, upd.getName()));
          scope.setEndpoint("/partial" + getEndpoint(upd.getName()));
          scope.setPkName(getPkName(table));
          scope.setPkType(getPkTypeName(table));

          scope.setUpdateRoles(new ArrayList<>(findRolesFor(upd, table)));

          return scope;
        })
        .collect(toList());
  }

  private Set<String> findRolesFor(PartialUpdate update, Table table) {
    Function<String, List<String>> getUpdateExpressionsForColumn =
        column -> permissionMap.getUpdateExpressionsFor(table.getName(), column);

    return update.getColumns().stream()
        .map(getUpdateExpressionsForColumn)
        .flatMap(Collection::stream)
        .collect(toSet());
  }

  @Override
  public String getPath() {
    return "rest-api/src/main/java/restapi/controller/updateController.java.ftl";
  }
}
