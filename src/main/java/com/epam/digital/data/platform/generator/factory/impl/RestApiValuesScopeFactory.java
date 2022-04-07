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

import com.epam.digital.data.platform.generator.factory.AbstractScope;
import com.epam.digital.data.platform.generator.metadata.ExposeSearchConditionOption;
import com.epam.digital.data.platform.generator.metadata.SearchConditionProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.scope.RestApiValuesScope;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class RestApiValuesScopeFactory extends AbstractScope<RestApiValuesScope> {

  private final JsonNode values;
  private final SearchConditionProvider searchConditionProvider;

  public RestApiValuesScopeFactory(
      JsonNode values, SearchConditionProvider searchConditionProvider) {
    this.values = values;
    this.searchConditionProvider = searchConditionProvider;
  }

  @Override
  public List<RestApiValuesScope> create(Context context) {
    RestApiValuesScope scope = new RestApiValuesScope();
    scope.setReplicationFactor(
        values.get("global").get("kafkaOperator").get("replicationFactor").asInt());
    String signer = Optional.ofNullable(values.get("s3"))
            .map(node -> node.get("config"))
            .map(node -> node.get("client"))
            .map(node -> node.get("signerOverride"))
            .map(JsonNode::textValue)
            .orElse(null);
    scope.setS3Signer(signer);
    scope.setExposedToPlatformPaths(
        getExposedSearchConditionPaths(ExposeSearchConditionOption.PLATFORM));
    scope.setExposedToExternalPaths(
        getExposedSearchConditionPaths(ExposeSearchConditionOption.EXTERNAL_SYSTEM));

    String stageName =
        Optional.ofNullable(values.get("stageName")).map(JsonNode::textValue).orElse(null);
    scope.setStageName(stageName);
    return List.of(scope);
  }

  private Set<String> getExposedSearchConditionPaths(ExposeSearchConditionOption option) {
    return searchConditionProvider.getExposedSearchConditions(option).stream()
            .map(this::getEndpoint)
            .collect(Collectors.toSet());
  }

  @Override
  public String getPath() {
    return "rest-api/deploy-templates/values.yaml.ftl";
  }
}
