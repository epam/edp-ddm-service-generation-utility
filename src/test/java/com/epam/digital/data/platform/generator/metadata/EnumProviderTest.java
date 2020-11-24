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

import static com.epam.digital.data.platform.generator.metadata.EnumProvider.ENUM_ATTRIBUTE_NAME;
import static com.epam.digital.data.platform.generator.metadata.EnumProvider.ENUM_CHANGE_TYPE;
import static com.epam.digital.data.platform.generator.metadata.EnumProvider.LABEL_CHANGE_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.epam.digital.data.platform.generator.model.template.EnumLabel;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EnumProviderTest {

  private static final String CHANGE_NAME_GENDER = "gender";
  private static final String CHANGE_NAME_WEEKEND = "weekend";

  private EnumProvider instance;

  @Mock
  private MetadataRepository metadataRepository;

  @BeforeEach
  public void setUp() {
    setupEnumMetadata(generateMetadata());
  }

  private void setupEnumMetadata(List<Metadata> metadata) {
    given(metadataRepository.findAll()).willReturn(metadata);
    instance = new EnumProvider(new MetadataFacade(metadataRepository));
  }

  private List<Metadata> generateMetadata() {
    return List.of(
        new Metadata(1L, ENUM_CHANGE_TYPE, CHANGE_NAME_GENDER, ENUM_ATTRIBUTE_NAME, "man"),
        new Metadata(2L, ENUM_CHANGE_TYPE, CHANGE_NAME_GENDER, ENUM_ATTRIBUTE_NAME, "woman"),
        new Metadata(3L, ENUM_CHANGE_TYPE, CHANGE_NAME_WEEKEND, ENUM_ATTRIBUTE_NAME, "saturday"),
        new Metadata(4L, ENUM_CHANGE_TYPE, CHANGE_NAME_WEEKEND, ENUM_ATTRIBUTE_NAME, "sunday")
    );
  }

  @Test
  void shouldFindGender() {
    var set = instance.findFor(CHANGE_NAME_GENDER);

    assertThat(set)
        .containsExactly(CHANGE_NAME_GENDER);
  }

  @Test
  void shouldFindAll() {
    var map = instance.findAllWithValues();

    assertThat(map)
        .hasSize(2)
        .containsKeys(CHANGE_NAME_GENDER, CHANGE_NAME_WEEKEND);

    assertThat(map.get(CHANGE_NAME_GENDER))
        .containsExactly("man", "woman");

    assertThat(map.get(CHANGE_NAME_WEEKEND))
        .containsExactly("saturday", "sunday");
  }

  @Test
  void shouldFindAllLabels() {
    // given
    var el1 = new EnumLabel("man", "чоловік");
    var el2 = new EnumLabel("woman", "жінка");
    var el3 = new EnumLabel("saturday", "субота");
    var el4 = new EnumLabel("sunday", "неділя");
    setupEnumMetadata(List.of(
        new Metadata(1L, LABEL_CHANGE_TYPE, CHANGE_NAME_GENDER, el1.getCode(), el1.getLabel()),
        new Metadata(2L, LABEL_CHANGE_TYPE, CHANGE_NAME_GENDER, el2.getCode(), el2.getLabel()),
        new Metadata(3L, LABEL_CHANGE_TYPE, CHANGE_NAME_WEEKEND, el3.getCode(), el3.getLabel()),
        new Metadata(4L, LABEL_CHANGE_TYPE, CHANGE_NAME_WEEKEND, el4.getCode(), el4.getLabel())
    ));

    // when
    var map = instance.findAllLabels();

    // then
    assertThat(map)
        .hasSize(2)
        .containsKeys(CHANGE_NAME_GENDER, CHANGE_NAME_WEEKEND);

    assertThat(map.get(CHANGE_NAME_GENDER))
        .hasSize(2)
        .containsExactly(el1, el2);

    assertThat(map.get(CHANGE_NAME_WEEKEND))
        .hasSize(2)
        .containsExactly(el3, el4);
  }
}
