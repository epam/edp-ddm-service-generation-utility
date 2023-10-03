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

package com.epam.digital.data.platform.generator.factory.impl;

import com.epam.digital.data.platform.generator.factory.AbstractApplicationYamlScope;
import com.epam.digital.data.platform.generator.metadata.AsyncDataLoadInfoProvider;
import com.epam.digital.data.platform.generator.metadata.BulkLoadInfoProvider;
import com.epam.digital.data.platform.generator.metadata.EnumProvider;
import com.epam.digital.data.platform.generator.metadata.NestedStructure;
import com.epam.digital.data.platform.generator.metadata.NestedStructureProvider;
import com.epam.digital.data.platform.generator.metadata.PartialUpdate;
import com.epam.digital.data.platform.generator.metadata.PartialUpdateProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.scope.RestApplicationYamlScope;
import com.epam.digital.data.platform.generator.utils.DbUtils;
import org.springframework.stereotype.Component;
import schemacrawler.schema.Table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Component
public class RestApplicationYamlScopeFactory
    extends AbstractApplicationYamlScope<RestApplicationYamlScope> {

  private final EnumProvider enumProvider;
  private final AsyncDataLoadInfoProvider asyncDataLoadInfoProvider;

  public RestApplicationYamlScopeFactory(
      PartialUpdateProvider partialUpdateProvider,
      NestedStructureProvider nestedStructureProvider,
      BulkLoadInfoProvider bulkLoadInfoProvider,
      AsyncDataLoadInfoProvider asyncDataLoadInfoProvider,
      EnumProvider enumProvider) {
    super(partialUpdateProvider, nestedStructureProvider, bulkLoadInfoProvider);
    this.enumProvider = enumProvider;
    this.asyncDataLoadInfoProvider = asyncDataLoadInfoProvider;
  }

  @Override
  public List<RestApplicationYamlScope> create(Context context) {
    List<RestApplicationYamlScope> result = super.create(context);
    result.get(0).setEntityPaths(getEntityPaths(context));
    result.get(0).setSearchPaths(getSearchPaths(context));
    result.get(0).setNestedPaths(getNestedInsertPaths());
    result.get(0).setEnumPresent(isEnumPresent());
    result.get(0).setRetentionPolicyDaysRead(context.getSettings().getKafka()
        .getRetentionPolicyInDays().getRead());
    result.get(0).setRetentionPolicyDaysWrite(context.getSettings().getKafka()
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

    var bulkLoadPaths = getBulkLoadPaths();
    entityPaths.forEach((k, v) -> ofNullable(bulkLoadPaths.get(k)).ifPresent(v::addAll));

    var asyncDataLoadPaths = getAsyncDataLoadPaths();
    entityPaths.forEach((k, v) -> ofNullable(asyncDataLoadPaths.get(k)).ifPresent(v::addAll));

    var fileEndpointPaths = getFileEndpointPaths(context);
    entityPaths.forEach((k, v) -> {
      var opt = ofNullable(fileEndpointPaths.get(k));
      opt.ifPresent(v::addAll);
    });

    return entityPaths;
  }

  private List<String> getSearchPaths(Context context) {
    return context.getCatalog().getTables().stream()
        .filter(this::isSearchConditionsView)
        .map(Table::getName)
        .map(this::toHyphenTableName)
        .collect(toList());
  }

  private Map<String, List<String>> getFileEndpointPaths(Context context) {
    return context.getCatalog().getTables().stream()
        .filter(this::isRecentDataTable)
        .collect(
            toMap(
                this::toHyphenTableName,
                table -> {
                  var fileColumnExists =
                      table.getColumns().stream().anyMatch(DbUtils::isColumnFileOrFileArray);
                  if (fileColumnExists) {
                    return Collections.singletonList("files" + getEndpoint(table.getName()));
                  }
                  return Collections.emptyList();
                }));
  }

  private Map<String, List<String>> getPartialUpdatePaths() {

    Function<PartialUpdate, String> tableName = upd -> toHyphenTableName(upd.getTableName());
    Function<PartialUpdate, String> endpoint = upd -> "partial" + getEndpoint(upd.getName());

    return partialUpdateProvider.findAll().stream()
        .collect(groupingBy(tableName, mapping(endpoint, toList())));
  }

  private Map<String, List<String>> getBulkLoadPaths() {
    return bulkLoadInfoProvider.getTablesWithBulkLoad().stream()
        .map(this::toHyphenTableName)
        .collect(
            toMap(
                Function.identity(),
                endpoint ->
                    List.of(endpoint + "/list", endpoint + "/csv", endpoint + "/csv/validation")));
  }

  private Map<String, List<String>> getAsyncDataLoadPaths() {
    return asyncDataLoadInfoProvider.getTablesWithAsyncLoad().keySet().stream()
        .map(this::toHyphenTableName)
        .collect(toMap(
            Function.identity(),
            endpoint -> List.of("v2/" + endpoint + "/csv/validation")));
  }

  private List<String> getNestedInsertPaths() {
    Function<String, String> endpointMapper =
        structureName -> "nested" + getEndpoint(structureName);

    var nestedStructures = nestedStructureProvider.findAll()
        .stream().map(NestedStructure::getName)
        .collect(Collectors.toSet());

    var nestedPaths = new ArrayList<String>();

    nestedStructures.stream()
        .map(endpointMapper)
        .forEach(nestedPaths::add);

    asyncDataLoadInfoProvider.getTablesWithAsyncLoad().keySet().stream()
        .filter(nestedStructures::contains)
        .map(this::toHyphenTableName)
        .map(nestedPath -> "v2/nested" + getEndpoint(nestedPath) + "/csv/validation")
        .forEach(nestedPaths::add);

    return nestedPaths;
  }

  private boolean isEnumPresent() {
    return !enumProvider.findAllLabels().isEmpty();
  }
}
