package com.epam.digital.data.platform.generator.scope;

import com.epam.digital.data.platform.generator.model.template.NestedCommandHandlerField;

import java.util.ArrayList;
import java.util.List;

public class NestedCommandHandlerScope extends ClassScope {

  private String schemaName;
  private String rootEntityName;
  private String rootHandler;
  private List<String> simpleFields = new ArrayList<>();
  private List<NestedCommandHandlerField> nestedHandlers = new ArrayList<>();

  public String getSchemaName() {
    return schemaName;
  }

  public void setSchemaName(String schemaName) {
    this.schemaName = schemaName;
  }

  public String getRootEntityName() {
    return rootEntityName;
  }

  public void setRootEntityName(String rootEntityName) {
    this.rootEntityName = rootEntityName;
  }

  public String getRootHandler() {
    return rootHandler;
  }

  public void setRootHandler(String rootHandler) {
    this.rootHandler = rootHandler;
  }

  public List<String> getSimpleFields() {
    return simpleFields;
  }

  public void setSimpleFields(List<String> simpleFields) {
    this.simpleFields = simpleFields;
  }

  public List<NestedCommandHandlerField> getNestedHandlers() {
    return nestedHandlers;
  }

  public void setNestedHandlers(List<NestedCommandHandlerField> nestedHandlers) {
    this.nestedHandlers = nestedHandlers;
  }
}
