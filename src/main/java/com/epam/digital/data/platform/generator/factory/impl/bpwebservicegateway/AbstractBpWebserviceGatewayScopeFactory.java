/*
 * Copyright 2023 EPAM Systems.
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

package com.epam.digital.data.platform.generator.factory.impl.bpwebservicegateway;

import com.epam.digital.data.platform.generator.model.TrembitaProcessDefinitions;

public abstract class AbstractBpWebserviceGatewayScopeFactory {

  private static final String PREFIX_START = "Start";
  protected static final String SUFFIX_REQUEST = "Request";
  protected static final String SUFFIX_RESPONSE = "Response";
  protected static final String DEFAULT_ENDPOINT = "Bp";

  protected final TrembitaProcessDefinitions processDefinitions;

  protected AbstractBpWebserviceGatewayScopeFactory(
      TrembitaProcessDefinitions processDefinitions) {
    this.processDefinitions = processDefinitions;
  }

  protected String generateClassName(String definitionId, String classNameSuffix) {
    return String.format("%s%s%s", PREFIX_START, definitionId, classNameSuffix);
  }

}
