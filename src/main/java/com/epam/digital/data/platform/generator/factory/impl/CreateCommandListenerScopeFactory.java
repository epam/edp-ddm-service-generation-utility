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

import com.epam.digital.data.platform.generator.factory.AbstractCommandListenerScopeFactory;
import org.springframework.stereotype.Component;

@Component
public class CreateCommandListenerScopeFactory extends AbstractCommandListenerScopeFactory {

  @Override
  protected String getOperation() {
    return "create";
  }

  @Override
  protected String getOutputType() {
    return "com.epam.digital.data.platform.model.core.kafka.EntityId";
  }

  @Override
  public String getPath() {
    return "kafka-api/src/main/java/kafkaapi/listener/createListener.java.ftl";
  }
}
