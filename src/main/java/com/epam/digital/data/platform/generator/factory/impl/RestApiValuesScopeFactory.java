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

import com.epam.digital.data.platform.generator.factory.ScopeFactory;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.scope.RestApiValuesScope;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class RestApiValuesScopeFactory implements ScopeFactory<RestApiValuesScope> {

  private final JsonNode values;

  public RestApiValuesScopeFactory(JsonNode values) {
    this.values = values;
  }

  @Override
  public List<RestApiValuesScope> create(Context context) {
    RestApiValuesScope scope = new RestApiValuesScope();
    scope.setReplicationFactor(
        values.get("global").get("kafkaOperator").get("replicationFactor").asInt());
    return List.of(scope);
  }

  @Override
  public String getPath() {
    return "rest-api/deploy-templates/values.yaml.ftl";
  }
}
