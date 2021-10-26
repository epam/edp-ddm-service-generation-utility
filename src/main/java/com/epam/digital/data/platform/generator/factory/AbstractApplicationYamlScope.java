package com.epam.digital.data.platform.generator.factory;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

import com.epam.digital.data.platform.generator.metadata.PartialUpdateProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.scope.ApplicationYamlScope;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractApplicationYamlScope<T extends ApplicationYamlScope>
    extends AbstractScope<T> {

  static final List<String> CRUD_REQUESTS = List.of("create", "update", "delete");

  private final PartialUpdateProvider partialUpdateProvider;

  protected AbstractApplicationYamlScope(
      PartialUpdateProvider partialUpdateProvider) {
    this.partialUpdateProvider = partialUpdateProvider;
  }

  protected abstract T instantiate();

  @Override
  public List<T> create(Context context) {
    var rootTopics = new ArrayList<String>();
    rootTopics.addAll(getCrudTopics(context));
    rootTopics.addAll(getPartialUpdateTopics(context));

    var applicationYamlScope = instantiate();
    applicationYamlScope.setRootsOfTopicNames(rootTopics);
    return singletonList(applicationYamlScope);
  }

  private List<String> getPartialUpdateTopics(Context context) {
    return partialUpdateProvider.findAll().stream()
        .map(upd -> {
          var table = findTable(upd.getTableName(), context);
          return "update-" + toHyphenTableName(table) + "-" + toHyphenTableName(upd.getName());
        })
        .collect(toList());
  }

  private List<String> getCrudTopics(Context context) {
    return context.getCatalog().getTables().stream()
        .filter(this::isRecentDataTable)
        .map(this::toHyphenTableName)
        .flatMap(topicRoot -> CRUD_REQUESTS.stream()
            .map(request -> request + "-" + topicRoot))
        .collect(toList());
  }
}
