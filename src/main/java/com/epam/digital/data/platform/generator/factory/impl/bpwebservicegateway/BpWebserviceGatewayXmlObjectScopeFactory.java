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

import com.epam.digital.data.platform.generator.factory.ScopeFactory;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.model.TrembitaProcessDefinitions;
import com.epam.digital.data.platform.generator.scope.BpWebserviceGatewayXmlObjectScope;
import com.epam.digital.data.platform.generator.utils.ProcessDefinitionIdConverter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BpWebserviceGatewayXmlObjectScopeFactory extends
    AbstractBpWebserviceGatewayScopeFactory implements
    ScopeFactory<BpWebserviceGatewayXmlObjectScope> {

  public BpWebserviceGatewayXmlObjectScopeFactory(
      TrembitaProcessDefinitions processDefinitions) {
    super(processDefinitions);
  }

  @Override
  public List<BpWebserviceGatewayXmlObjectScope> create(Context context) {
    var scope = new BpWebserviceGatewayXmlObjectScope();
    var names = new ArrayList<String>();
    names.addAll(generateClassNames(SUFFIX_REQUEST));
    names.addAll(generateClassNames(SUFFIX_RESPONSE));
    names.add(generateClassName(DEFAULT_ENDPOINT, SUFFIX_REQUEST));
    names.add(generateClassName(DEFAULT_ENDPOINT, SUFFIX_RESPONSE));
    scope.setClassNames(names);
    return List.of(scope);
  }

  private List<String> generateClassNames(String classNameSuffix) {
    return processDefinitions.getProcessDefinitions().stream()
        .map(pd -> {
          var convertedId = ProcessDefinitionIdConverter.convert(pd.getProcessDefinitionId());
          return generateClassName(convertedId, classNameSuffix);
        }).collect(Collectors.toList());
  }

  @Override
  public String getPath() {
    return "bp-webservice-gateway/src/main/java/bpwebservice/soap/factory/XmlObjectFactory.java.ftl";
  }
}
