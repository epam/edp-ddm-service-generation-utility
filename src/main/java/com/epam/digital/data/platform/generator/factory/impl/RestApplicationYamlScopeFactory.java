package com.epam.digital.data.platform.generator.factory.impl;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

import com.epam.digital.data.platform.generator.factory.AbstractApplicationYamlScope;
import com.epam.digital.data.platform.generator.metadata.EnumProvider;
import com.epam.digital.data.platform.generator.metadata.PartialUpdate;
import com.epam.digital.data.platform.generator.metadata.PartialUpdateProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.scope.RestApplicationYamlScope;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import schemacrawler.schema.Table;

@Component
public class RestApplicationYamlScopeFactory
    extends AbstractApplicationYamlScope<RestApplicationYamlScope> {

  private final EnumProvider enumProvider;
  private final PartialUpdateProvider partialUpdateProvider;

  public RestApplicationYamlScopeFactory(
      EnumProvider enumProvider,
      PartialUpdateProvider partialUpdateProvider) {
    super(partialUpdateProvider);
    this.enumProvider = enumProvider;
    this.partialUpdateProvider = partialUpdateProvider;
  }

  @Override
  public List<RestApplicationYamlScope> create(Context context) {
    List<RestApplicationYamlScope> result = super.create(context);
    result.get(0).setEntityPaths(getEntityPaths(context));
    result.get(0).setSearchPaths(getSearchPaths(context));
    result.get(0).setEnumPresent(isEnumPresent());
    result.get(0).setRetentionPolicyDaysRead(context.getBlueprint().getSettings().getKafka()
        .getRetentionPolicyInDays().getRead());
    result.get(0).setRetentionPolicyDaysWrite(context.getBlueprint().getSettings().getKafka()
        .getRetentionPolicyInDays().getWrite());
    return result;
  }

  @Override
  public String getPath() {
    return "rest-api/src/main/resources/application.yaml.ftl";
  }

  @Override
  protected RestApplicationYamlScope instantiate() {
    return new RestApplicationYamlScope();
  }

  private Map<String, List<String>> getEntityPaths(Context context) {
    Function<String, List<String>> asList = str -> new ArrayList<>(Collections.singletonList(str));

    var entityPaths = context.getCatalog().getTables().stream()
        .filter(this::isRecentDataTable)
        .map(Table::getName)
        .map(this::toHyphenTableName)
        .collect(Collectors.toMap(Function.identity(), asList));

    var partialUpdatePaths = getPartialUpdatePaths();

    entityPaths.forEach((k, v) -> ofNullable(partialUpdatePaths.get(k)).ifPresent(v::addAll));
    return entityPaths;
  }

  private List<String> getSearchPaths(Context context) {
    return context.getCatalog().getTables().stream()
        .filter(this::isSearchConditionsView)
        .map(Table::getName)
        .map(this::toHyphenTableName)
        .collect(toList());
  }

  private Map<String, List<String>> getPartialUpdatePaths() {

    Function<PartialUpdate, String> tableName = upd -> toHyphenTableName(upd.getTableName());
    Function<PartialUpdate, String> endpoint = upd -> "partial" + getEndpoint(upd.getName());

    return partialUpdateProvider.findAll().stream()
        .collect(groupingBy(tableName, mapping(endpoint, toList())));
  }

  private boolean isEnumPresent() {
    return !enumProvider.findAllLabels().isEmpty();
  }
}
