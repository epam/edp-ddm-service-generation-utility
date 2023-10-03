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

package com.epam.digital.data.platform.generator.config.dblessmode;

import com.epam.digital.data.platform.generator.factory.DefaultScopeFactory;
import com.epam.digital.data.platform.generator.factory.ScopeFactory;
import com.epam.digital.data.platform.generator.factory.impl.bpwebservicegateway.BpWebserviceGatewaySoapDtoScopeFactory;
import com.epam.digital.data.platform.generator.factory.impl.bpwebservicegateway.BpWebserviceGatewaySoapEndpointScopeFactory;
import com.epam.digital.data.platform.generator.factory.impl.bpwebservicegateway.BpWebserviceGatewayXmlObjectScopeFactory;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.model.Settings;
import com.epam.digital.data.platform.generator.model.TrembitaProcessDefinitions;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;

@Configuration
@Profile("db-less-mode")
@ComponentScan("com.epam.digital.data.platform.generator.config.general")
public class DbLessModeBeanConfig {

  @Bean
  public TrembitaProcessDefinitions processDefinitions(
      @Value("file:#{systemProperties.definitions}") Resource file, ObjectMapper mapper)
      throws IOException {

    var objectReader = mapper.reader().withRootName("trembita");
    try (var fis = new FileInputStream(file.getFile())) {
      return objectReader.readValue(fis, TrembitaProcessDefinitions.class);
    }
  }

  @Bean
  DefaultScopeFactory defaultScopeFactory(List<ScopeFactory<?>> factories) {
    return new DefaultScopeFactory(factories);
  }

  @Bean
  public Context context(Settings settings) {
    return new Context(settings);
  }

  @Bean
  BpWebserviceGatewaySoapEndpointScopeFactory bpWebserviceGatewaySoapEndpointScopeFactory(
      TrembitaProcessDefinitions processDefinitions) {
    return new BpWebserviceGatewaySoapEndpointScopeFactory(processDefinitions);
  }

  @Bean
  BpWebserviceGatewaySoapDtoScopeFactory bpWebserviceGatewaySoapEntityScopeFactory(
      TrembitaProcessDefinitions processDefinitions) {
    return new BpWebserviceGatewaySoapDtoScopeFactory(processDefinitions);
  }

  @Bean
  BpWebserviceGatewayXmlObjectScopeFactory bpWebserviceGatewaySoapObjectfactoryScopeFactory(
      TrembitaProcessDefinitions processDefinitions) {
    return new BpWebserviceGatewayXmlObjectScopeFactory(processDefinitions);
  }
}
