package com.epam.digital.data.platform.generator.metadata;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableMap;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toUnmodifiableList;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;

@Component
public class MetadataFacade {

  private final Map<String, List<Metadata>> rows;

  public MetadataFacade(MetadataRepository repository) {
    var map = repository.findAll().stream()
        .collect(groupingBy(Metadata::getChangeType, mapping(identity(), toUnmodifiableList())));

    this.rows = unmodifiableMap(map);
  }

  public Stream<Metadata> findByChangeType(String changeType) {
    return getOrEmpty(changeType).stream();
  }

  public Stream<Metadata> findByChangeTypeAndChangeName(String changeType, String changeName) {
    return filter(changeType, x -> x.getChangeName().equals(changeName));
  }

  public Stream<Metadata> findByChangeTypeAndName(String changeType, String name) {
    return filter(changeType, x -> x.getName().equals(name));
  }

  public Stream<Metadata> findByChangeTypeAndChangeNameAndName(String changeType, String changeName,
      String name) {
    return filter(changeType,
        x -> x.getChangeName().equals(changeName) && x.getName().equals(name));
  }

  private Stream<Metadata> filter(String changeType, Predicate<Metadata> p) {
    return getOrEmpty(changeType).stream().filter(p);
  }

  private List<Metadata> getOrEmpty(String changeType) {
    return rows.getOrDefault(changeType, emptyList());
  }

  public Stream<Metadata> findByChangeTypeAndValueIn(String changeType, List<String> values) {
    return filter(changeType,
        x -> values.contains(x.getValue()));
  }
}
