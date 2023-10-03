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

import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.getContext;

import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.model.TrembitaProcessDefinitions;

public abstract class AbstractBpWebserviceGatewayTest {

  protected final Context context = getContext();

  protected TrembitaProcessDefinitions getDefinitions() {
    var definitions = new TrembitaProcessDefinitions();
    definitions.addProcessDefinitionId("feature-create-school");
    return definitions;
  }
}
