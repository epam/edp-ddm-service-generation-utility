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
import com.epam.digital.data.platform.generator.model.template.SoapEndpointParameters;
import com.epam.digital.data.platform.generator.scope.BpWebserviceGatewayEndpointScope;
import com.epam.digital.data.platform.generator.utils.ProcessDefinitionIdConverter;
import java.util.List;
import java.util.stream.Collectors;

public class BpWebserviceGatewaySoapEndpointScopeFactory extends
    AbstractBpWebserviceGatewayScopeFactory implements
    ScopeFactory<BpWebserviceGatewayEndpointScope> {

  public BpWebserviceGatewaySoapEndpointScopeFactory(
      TrembitaProcessDefinitions processDefinitions) {
    super(processDefinitions);
  }

  @Override
  public List<BpWebserviceGatewayEndpointScope> create(Context context) {
    var resultScope = new BpWebserviceGatewayEndpointScope();
    var parameters = processDefinitions.getProcessDefinitions().stream()
        .map(pd -> {
          var convertedId = ProcessDefinitionIdConverter.convert(pd.getProcessDefinitionId());
          var param = new SoapEndpointParameters();
          param.setRequestClassName(generateClassName(convertedId, SUFFIX_REQUEST));
          param.setResponseClassName(generateClassName(convertedId, SUFFIX_RESPONSE));
          param.setMethodName(convertedId);
          param.setProcessDefinitionId(pd.getProcessDefinitionId());
          return param;
        }).collect(Collectors.toList());
    var defaultSoapEndpointParameter = new SoapEndpointParameters();
    defaultSoapEndpointParameter.setRequestClassName(
        generateClassName(DEFAULT_ENDPOINT, SUFFIX_REQUEST));
    defaultSoapEndpointParameter.setResponseClassName(
        generateClassName(DEFAULT_ENDPOINT, SUFFIX_RESPONSE));
    defaultSoapEndpointParameter.setMethodName(DEFAULT_ENDPOINT);
    resultScope.setDefaultEndpointParameter(defaultSoapEndpointParameter);
    resultScope.setEndpointParameters(parameters);
    return List.of(resultScope);
  }

  @Override
  public String getPath() {
    return "bp-webservice-gateway/src/main/java/bpwebservice/soap/wsendpoint/SoapEndpointHandler.java.ftl";
  }
}
