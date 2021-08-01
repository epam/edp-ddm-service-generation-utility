package com.epam.digital.data.platform.generator.factory;

import static java.util.stream.Collectors.groupingBy;

import com.epam.digital.data.platform.generator.utils.PathConverter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class DefaultScopeFactory {

  private final Map<String, ScopeFactory<?>> templateFactory = new HashMap<>();

  public DefaultScopeFactory(List<ScopeFactory<?>> factories) {
    Map<String, List<ScopeFactory<?>>> groupedByPath = factories.stream()
        .collect(groupingBy(this::safePath));

    for (var entry : groupedByPath.entrySet()) {
      if (entry.getValue().size() > 1) {
        templateFactory.put(entry.getKey(), new CompositeScopeFactory(entry.getValue()));
      } else {
        templateFactory.put(entry.getKey(), entry.getValue().get(0));
      }
    }
  }

  private String safePath(ScopeFactory<?> value) {
    return PathConverter.safePath(value.getPath());
  }

  @SuppressWarnings("rawtypes")
  public Scope create(String temp) {
    return templateFactory.get(temp);
  }
}
