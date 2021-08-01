package com.epam.digital.data.platform.generator.metadata;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import com.epam.digital.data.platform.generator.model.template.EnumLabel;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class EnumProvider {

    static final String ENUM_CHANGE_TYPE = "type";
    static final String ENUM_ATTRIBUTE_NAME = "label";

    static final String LABEL_CHANGE_TYPE = "label";

    private final MetadataFacade metadataFacade;

    public EnumProvider(MetadataFacade metadataFacade) {
        this.metadataFacade = metadataFacade;
    }

    public Map<String, List<String>> findAllWithValues() {
        return metadataFacade.findByChangeTypeAndName(ENUM_CHANGE_TYPE, ENUM_ATTRIBUTE_NAME)
            .collect(groupingBy(
                Metadata::getChangeName,
                mapping(Metadata::getValue,
                    toList())));
    }

    public Set<String> findFor(String name) {
        return metadataFacade
            .findByChangeTypeAndChangeNameAndName(ENUM_CHANGE_TYPE, name, ENUM_ATTRIBUTE_NAME)
            .map(Metadata::getChangeName)
            .collect(toSet());
    }

    public Map<String, List<EnumLabel>> findAllLabels() {
        return metadataFacade.findByChangeType(LABEL_CHANGE_TYPE)
            .collect(groupingBy(
                Metadata::getChangeName,
                mapping(x -> new EnumLabel(x.getName(), x.getValue()),
                    toList())));
    }
}
