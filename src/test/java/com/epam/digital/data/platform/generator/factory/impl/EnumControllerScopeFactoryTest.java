package com.epam.digital.data.platform.generator.factory.impl;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.epam.digital.data.platform.generator.metadata.EnumProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.model.template.EnumLabel;
import com.epam.digital.data.platform.generator.utils.ContextTestUtils;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import schemacrawler.schema.Catalog;

@ExtendWith(MockitoExtension.class)
class EnumControllerScopeFactoryTest {

  static final String MY_ENUM = "my-enum";
  static final String MY_ENUM_ENDPOINT = "my-enum";
  static final String MY_ENUM_METHOD_NAME = "myEnum";

  @Mock
  EnumProvider enumProvider;

  EnumControllerScopeFactory instance;

  Catalog catalog = ContextTestUtils.newCatalog();
  Context ctx = new Context(null, catalog);

  EnumLabel[] labelArr = {new EnumLabel("I", "Внесено"), new EnumLabel("D", "Видалено")};

  @BeforeEach
  void setup() {
    instance = new EnumControllerScopeFactory(enumProvider);
  }

  Map<String, List<EnumLabel>> setupLabels() {
    return Map.of(MY_ENUM, asList(labelArr));
  }

  @Test
  void shouldCreateScope() {
    // given
    given(enumProvider.findAllLabels()).willReturn(setupLabels());

    // when
    var scopes = instance.create(ctx);

    // then
    assertThat(scopes).hasSize(1);

    var endpoints = scopes.get(0).getEndpoints();
    assertThat(endpoints).hasSize(1);

    var endpoint = endpoints.get(0);
    assertThat(endpoint.getEndpoint()).isEqualTo(MY_ENUM_ENDPOINT);
    assertThat(endpoint.getMethodName()).isEqualTo(MY_ENUM_METHOD_NAME);
    assertThat(endpoint.getLabels()).containsExactly(labelArr);
  }
}
