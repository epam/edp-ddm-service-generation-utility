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
import com.epam.digital.data.platform.generator.scope.KafkaApiValuesScope;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class KafkaApiValuesScopeFactory implements ScopeFactory<KafkaApiValuesScope> {

  private final JsonNode values;

  public KafkaApiValuesScopeFactory(JsonNode values) {
    this.values = values;
  }

  @Override
  public List<KafkaApiValuesScope> create(Context context) {
    var scope = new KafkaApiValuesScope();
    String signer =
        Optional.ofNullable(values.get("s3"))
            .map(node -> node.get("config"))
            .map(node -> node.get("client"))
            .map(node -> node.get("signerOverride"))
            .map(JsonNode::textValue)
            .orElse(null);
    scope.setS3Signer(signer);
    return List.of(scope);
  }

  @Override
  public String getPath() {
    return "kafka-api/deploy-templates/values.yaml.ftl";
  }
}
