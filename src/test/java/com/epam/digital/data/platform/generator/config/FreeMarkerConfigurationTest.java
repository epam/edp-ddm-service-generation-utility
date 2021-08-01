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
    var config = instance.getConfig(getContext().getBlueprint());

    assertThat(((SimpleScalar) config.getSharedVariable("fullVersion")).getAsString())
        .isEqualTo(VERSION);
    assertThat(((SimpleScalar) config.getSharedVariable("serviceVersion")).getAsString())
        .isEqualTo(MAJOR_VERSION);
  }

  @Test
  void shouldThrowIllegalArgumentExceptionWhenCanNotExtractMajorVersion() {
    var blueprint = getContext().getBlueprint();
    blueprint.getSettings().getGeneral().setVersion("1");

    assertThatThrownBy(() -> instance.getConfig(blueprint))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
