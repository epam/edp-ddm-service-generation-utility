package com.epam.digital.data.platform.generator.factory.impl;

import static java.util.stream.Collectors.toList;

import com.epam.digital.data.platform.generator.factory.AbstractScope;
import com.epam.digital.data.platform.generator.metadata.PartialUpdateProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.scope.UpdateServiceScope;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PartialUpdateServiceScopeFactory extends AbstractScope<UpdateServiceScope> {

  private final PartialUpdateProvider partialUpdateProvider;

  public PartialUpdateServiceScopeFactory(
      PartialUpdateProvider partialUpdateProvider) {
    this.partialUpdateProvider = partialUpdateProvider;
  }

  @Override
  public List<UpdateServiceScope> create(Context context) {
    return partialUpdateProvider.findAll().stream()
        .map(upd -> {
          var table = findTable(upd.getTableName(), context);

          var scope = new UpdateServiceScope();
          scope.setClassName(getSchemaName(table, upd.getName()) + "UpdateService");
          scope.setSchemaName(getSchemaName(table, upd.getName()));
          scope.setPkName(getPkName(table));
          scope.setPkType(getPkTypeName(table));
          scope.setRequestType(
              "update-" + toHyphenTableName(table) + "-" + toHyphenTableName(upd.getName()));
          return scope;
        })
        .collect(toList());
  }

  @Override
  public String getPath() {
    return "rest-api/src/main/java/restapi/service/update.java.ftl";
  }
}
