package com.epam.digital.data.platform.generator.factory;

public interface ScopeFactory<T> extends Scope<T> {

  String getPath();
}
