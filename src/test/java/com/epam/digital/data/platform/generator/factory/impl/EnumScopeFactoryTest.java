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

package com.epam.digital.data.platform.generator.factory.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.epam.digital.data.platform.generator.metadata.EnumProvider;
import com.epam.digital.data.platform.generator.model.Context;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EnumScopeFactoryTest {

    @Mock
    private EnumProvider enumProvider;

    @InjectMocks
    private EnumScopeFactory enumScopeFactory;

    private Context context = new Context(null, null);

    @Test
    @DisplayName("EnumScopeFactory successful path")
    void shouldCreateCorrectScope() {
        Mockito.when(enumProvider.findAllWithValues()).thenReturn(mockEnumMap());

        var scopes = enumScopeFactory.create(context);
        assertEquals(1, scopes.size());

        var result = scopes.get(0);
        assertEquals(2, result.getValues().size());
        assertEquals("EnumFirst", result.getClassName());
        assertThat(result.getValues()).containsExactlyInAnyOrder("first", "second");
    }

    private Map<String, List<String>> mockEnumMap() {
        return Map.of("enum_first", List.of("first", "second"));
    }
}
