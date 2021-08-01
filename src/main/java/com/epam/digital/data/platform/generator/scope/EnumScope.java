package com.epam.digital.data.platform.generator.scope;

import java.util.HashSet;
import java.util.Set;

public class EnumScope extends ClassScope {

    private Set<String> values = new HashSet<>();

    public Set<String> getValues() {
        return values;
    }

    public void setValues(Set<String> values) {
        this.values = values;
    }
}
