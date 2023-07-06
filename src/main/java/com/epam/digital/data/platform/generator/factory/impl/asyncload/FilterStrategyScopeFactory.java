/*
 * Copyright 2023 EPAM Systems.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.epam.digital.data.platform.generator.factory.impl.asyncload;

import com.epam.digital.data.platform.generator.metadata.AsyncDataLoadInfoProvider;
import com.epam.digital.data.platform.generator.metadata.NestedStructureProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.scope.FilterStrategyScope;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.text.CaseUtils;
import org.springframework.stereotype.Component;

@Component
public class FilterStrategyScopeFactory extends
    AbstractAsyncDataLoadScopeFactory<FilterStrategyScope> {

  public FilterStrategyScopeFactory(
      AsyncDataLoadInfoProvider asyncDataLoadInfoProvider,
      NestedStructureProvider nestedStructureProvider) {
    super(asyncDataLoadInfoProvider, nestedStructureProvider);
  }

  @Override
  public List<FilterStrategyScope> create(Context context) {
    var scope = new FilterStrategyScope();
    scope.setClassName("AsyncDataLoadListenerFilterStrategy");
    scope.setAllowedEntities(asyncDataLoadInfoProvider.getTablesWithAsyncLoad().keySet()
        .stream().map(entityName -> CaseUtils.toCamelCase(entityName, true, '-', '_'))
        .collect(Collectors.toSet()));
    return List.of(scope);
  }

  @Override
  public String getPath() {
    return "kafka-api/src/main/java/kafkaapi/listener/filter/filterStrategy.java.ftl";
  }
}
