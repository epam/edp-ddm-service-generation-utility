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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.MAJOR_VERSION;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.VERSION;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.getContext;

import freemarker.template.SimpleScalar;
import freemarker.template.TemplateModelException;
import org.junit.jupiter.api.Test;

class FreeMarkerConfigurationTest {

  FreeMarkerConfiguration instance = new FreeMarkerConfiguration();

  @Test
  void shouldExtractMajorVersion() throws TemplateModelException {
    var config = instance.getConfig(getContext().getSettings());

    assertThat(((SimpleScalar) config.getSharedVariable("fullVersion")).getAsString())
        .isEqualTo(VERSION);
    assertThat(((SimpleScalar) config.getSharedVariable("serviceVersion")).getAsString())
        .isEqualTo(MAJOR_VERSION);
  }

  @Test
  void shouldThrowIllegalArgumentExceptionWhenCanNotExtractMajorVersion() {
    var settings = getContext().getSettings();
    settings.getGeneral().setVersion("1");

    assertThatThrownBy(() -> instance.getConfig(settings))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
