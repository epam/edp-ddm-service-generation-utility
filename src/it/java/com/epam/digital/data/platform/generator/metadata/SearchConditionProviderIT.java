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

package com.epam.digital.data.platform.generator.metadata;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.epam.digital.data.platform.generator.config.DataSourceEnable;

@DataSourceEnable
@SpringBootTest(classes = {
    SearchConditionProvider.class, MetadataFacade.class, RlsMetadataFacade.class
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
    assertThat(sc.getStartsWithArray()).hasSize(1);
    assertThat(sc.getContains()).hasSize(1);
    assertThat(sc.getLimit()).isEqualTo(1);
  }
}
