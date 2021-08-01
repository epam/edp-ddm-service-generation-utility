package com.epam.digital.data.platform.generator.exception;

public class ScopeInstantiationException extends RuntimeException {

  public ScopeInstantiationException(Exception e) {
    super("ScopeFactory instantiation exception", e);
  }
}
