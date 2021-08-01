package com.epam.digital.data.platform.generator.scope;

import java.util.ArrayList;
import java.util.List;

public class ListenerScope extends ClassScope {

  private String schemaName;
  private String pkType;
  private List<ListenerDetails> listeners = new ArrayList<>();

  public String getSchemaName() {
    return schemaName;
  }

  public void setSchemaName(String schemaName) {
    this.schemaName = schemaName;
  }

  public String getPkType() {
    return pkType;
  }

  public void setPkType(String pkType) {
    this.pkType = pkType;
  }

  public List<ListenerDetails> getListeners() {
    return listeners;
  }

  public void setListeners(
      List<ListenerDetails> listeners) {
    this.listeners = listeners;
  }

  public void addListener(String operation, String rootOfTopicName, String inputType,
      String outputType) {
    this.listeners.add(new ListenerDetails(operation, rootOfTopicName, inputType, outputType));
  }
}
