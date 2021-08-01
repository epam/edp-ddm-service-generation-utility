package com.epam.digital.data.platform.generator.scope;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SoapScope {

  private Set<String> schemaNames = new HashSet<>();
  private List<ControllerScope> searchScopes = new ArrayList<>();

  public Set<String> getSchemaNames() {
    return schemaNames;
  }

  public List<ControllerScope> getSearchScopes() {
    return searchScopes;
  }

  public void addSchemaName(String schemaName) {
    schemaNames.add(schemaName);
  }

  public void addSearchScopes(ControllerScope searchScope) {
    searchScopes.add(searchScope);
  }
}
