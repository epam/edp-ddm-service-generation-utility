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
