package com.epam.digital.data.platform.generator.factory.impl;

import static java.util.stream.Collectors.toList;

import com.epam.digital.data.platform.generator.metadata.EnumProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.scope.EnumScope;
import java.util.HashSet;
import java.util.List;
import org.springframework.stereotype.Component;
import com.epam.digital.data.platform.generator.factory.AbstractScope;

@Component
public class EnumScopeFactory extends AbstractScope<EnumScope> {

    private final EnumProvider enumProvider;

    public EnumScopeFactory(EnumProvider enumProvider) {
        this.enumProvider = enumProvider;
    }

    @Override
    public List<EnumScope> create(Context context) {
        return enumProvider.findAllWithValues().entrySet().stream()
            .map(entry -> {
                var scope = new EnumScope();
                scope.setClassName(getSchemaName(entry.getKey()));
                scope.setValues(new HashSet<>(entry.getValue()));
                return scope;
            }).collect(toList());
    }

    @Override
    public String getPath() {
        return "model/src/main/java/model/dto/enum.java.ftl";
    }
}
