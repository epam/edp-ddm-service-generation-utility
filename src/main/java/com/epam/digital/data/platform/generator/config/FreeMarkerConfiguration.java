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

package com.epam.digital.data.platform.generator.config;

import com.epam.digital.data.platform.generator.model.Settings;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateModelException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FreeMarkerConfiguration {

  @Bean
  public freemarker.template.Configuration getConfig(Settings settings)
      throws TemplateModelException {
    freemarker.template.Configuration configuration = new freemarker.template.Configuration(
        freemarker.template.Configuration.VERSION_2_3_29);
    configuration.setDefaultEncoding("UTF-8");
    configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    configuration.setLogTemplateExceptions(false);
    configuration.setWrapUncheckedExceptions(true);
    configuration.setFallbackOnNullLoopVariable(false);
    configuration.setLocalizedLookup(false);
    configuration.setSharedVariable("basePackage",
        settings.getGeneral().getBasePackageName());
    configuration.setSharedVariable("register",
        settings.getGeneral().getRegister());
    configuration.setTemplateLoader(new SimpleClasspathLoader());

    var version = settings.getGeneral().getVersion();
    configuration.setSharedVariable("fullVersion", version);
    var majorVersion = extractMajorVersion(version);
    configuration.setSharedVariable("serviceVersion", majorVersion);

    return configuration;
  }

  private String extractMajorVersion(String version) {
    String[] splitted = version.split("\\.");
    if (splitted.length < 2) {
      throw new IllegalArgumentException("Can not extract major version from: " + version);
    }
    return splitted[0] + "." + splitted[1];
  }
}
