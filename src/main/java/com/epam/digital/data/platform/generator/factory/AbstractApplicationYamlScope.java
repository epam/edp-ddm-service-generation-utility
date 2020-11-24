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

package com.epam.digital.data.platform.generator.factory;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

import com.epam.digital.data.platform.generator.metadata.NestedStructureProvider;
import com.epam.digital.data.platform.generator.metadata.PartialUpdateProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.scope.ApplicationYamlScope;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class AbstractApplicationYamlScope<T extends ApplicationYamlScope>
    extends AbstractScope<T> {

  static final List<String> CUD_REQUESTS = List.of("create", "update", "delete");
  static final String READ_OPERATION = "read";
  static final String SEARCH_OPERATION = "search";

  protected final PartialUpdateProvider partialUpdateProvider;
  protected final NestedStructureProvider nestedStructureProvider;

  protected AbstractApplicationYamlScope(
      PartialUpdateProvider partialUpdateProvider,
      NestedStructureProvider nestedStructureProvider) {
    this.partialUpdateProvider = partialUpdateProvider;
    this.nestedStructureProvider = nestedStructureProvider;
  }

  protected abstract T instantiate();

  @Override
  public List<T> create(Context context) {
    var rootTopics = new ArrayList<String>();
    rootTopics.addAll(getCudTopics(context));
    rootTopics.addAll(getPartialUpdateTopics(context));
    rootTopics.addAll(getNestedTopics());
    if (!context.getAsyncData().getAsyncTables().isEmpty()) {
      rootTopics.addAll(getAsyncTopics(context.getAsyncData().getAsyncTables(), READ_OPERATION));
    }
    if (!context.getAsyncData().getAsyncSearchConditions().isEmpty()){
      rootTopics.addAll(getAsyncTopics(context.getAsyncData().getAsyncSearchConditions(), SEARCH_OPERATION));
    }

    var applicationYamlScope = instantiate();
    applicationYamlScope.setRootsOfTopicNames(rootTopics);
    return singletonList(applicationYamlScope);
  }

  private List<String> getPartialUpdateTopics(Context context) {
    return partialUpdateProvider.findAll().stream()
        .map(upd -> {
          var table = findTable(upd.getTableName(), context);
          return "update-" + toHyphenTableName(table) + "-" + toHyphenTableName(upd.getName());
        })
        .collect(toList());
  }

  private List<String> getCudTopics(Context context) {
    return context.getCatalog().getTables().stream()
        .filter(this::isRecentDataTable)
        .map(this::toHyphenTableName)
        .flatMap(topicRoot -> CUD_REQUESTS.stream()
            .map(request -> request + "-" + topicRoot))
        .collect(toList());
  }

  private List<String> getAsyncTopics(Set<String> asyncData, String operation) {
    return asyncData.stream()
        .map(name -> operation + "-" + toHyphenTableName(name))
        .collect(toList());
  }

  private List<String> getNestedTopics() {
    return nestedStructureProvider.findAll().stream()
        .map(
            nestedStructure ->
                "upsert-"
                    + toHyphenTableName(nestedStructure.getName())
                    + "-"
                    + toHyphenTableName(nestedStructure.getRoot().getTableName())
                    + "-nested")
        .collect(toList());
  }
}
