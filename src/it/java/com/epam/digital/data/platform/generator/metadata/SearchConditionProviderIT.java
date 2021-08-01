package com.epam.digital.data.platform.generator.metadata;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.epam.digital.data.platform.generator.config.DataSourceEnable;

@DataSourceEnable
@SpringBootTest(classes = {
    SearchConditionProvider.class, MetadataFacade.class
})
class SearchConditionProviderIT {

  @Autowired
  private SearchConditionProvider instance;

  @Test
  void shouldFindByChangeName() {
    var sc = instance.findFor("pd_processing_consent_2");

    assertThat(sc.getEqual()).hasSize(2);
    assertThat(sc.getEqual().get(0)).isEqualTo("person_full_name");
    assertThat(sc.getEqual().get(1)).isEqualTo("person_pass_number");
    assertThat(sc.getStartsWith()).hasSize(1);
    assertThat(sc.getContains()).hasSize(1);
    assertThat(sc.getLimit()).isEqualTo(1);
  }
}
