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
