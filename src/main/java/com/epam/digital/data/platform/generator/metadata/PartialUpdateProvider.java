package com.epam.digital.data.platform.generator.metadata;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.util.HashSet;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PartialUpdateProvider {

  static final String PARTIAL_UPDATE_CHANGE_TYPE = "partialUpdate";

  private final MetadataFacade metadataFacade;

  public PartialUpdateProvider(MetadataFacade metadataFacade) {
    this.metadataFacade = metadataFacade;
  }

  public List<PartialUpdate> findAll() {
    var mappedByName = metadataFacade
        .findByChangeType(PARTIAL_UPDATE_CHANGE_TYPE)
        .collect(groupingBy(Metadata::getChangeName));

    return mappedByName.values().stream()
        .map(this::toPartialUpdateObj)
        .collect(toList());
  }

  private PartialUpdate toPartialUpdateObj(List<Metadata> list) {
    var updateName = list.get(0).getChangeName();
    var tableName = list.get(0).getName();
    var columns = list.stream().map(Metadata::getValue).collect(toList());
    return new PartialUpdate(updateName, tableName, new HashSet<>(columns));
  }
}
