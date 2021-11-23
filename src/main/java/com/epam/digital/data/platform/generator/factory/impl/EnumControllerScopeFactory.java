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

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

import com.epam.digital.data.platform.generator.metadata.EnumProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.model.template.EnumEndpoint;
import com.epam.digital.data.platform.generator.model.template.EnumLabel;
import com.epam.digital.data.platform.generator.scope.EnumControllerScope;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.springframework.stereotype.Component;
import com.epam.digital.data.platform.generator.factory.AbstractScope;

@Component
public class EnumControllerScopeFactory extends AbstractScope<EnumControllerScope> {

  private final EnumProvider enumProvider;

  public EnumControllerScopeFactory(
      EnumProvider enumProvider) {
    this.enumProvider = enumProvider;
  }

  @Override
  public List<EnumControllerScope> create(Context context) {
    Map<String, List<EnumLabel>> labels = enumProvider.findAllLabels();

    List<EnumEndpoint> endpoints = labels.entrySet().stream()
        .map(this::toEnumEndpoint)
        .collect(toList());

    var scope = new EnumControllerScope();
    scope.setClassName("EnumController");
    scope.setEndpoints(endpoints);
    return singletonList(scope);
  }

  private EnumEndpoint toEnumEndpoint(Entry<String, List<EnumLabel>> e) {
    return new EnumEndpoint(
        toHyphenTableName(e.getKey()),
        getPropertyName(e.getKey()),
        e.getValue());
  }

  @Override
  public String getPath() {
    return "rest-api/src/main/java/restapi/controller/enumController.java.ftl";
  }
}
