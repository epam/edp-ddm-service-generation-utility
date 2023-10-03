package com.epam.digital.data.platform.generator.permissionmap;

import java.util.function.Predicate;

public enum PermissionObjectType {

  TABLE(x -> x.getObjectType().equals("table")),
  SEARCH_CONDITION(x -> x.getObjectType().equals("search_condition"));

  private final Predicate<Permission> predicate;

  PermissionObjectType(Predicate<Permission> predicate) {
    this.predicate = predicate;
  }

  public Predicate<Permission> getPredicate(){
    return this.predicate;
  }
}
