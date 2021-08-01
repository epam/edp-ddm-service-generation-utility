package com.epam.digital.data.platform.generator.factory.impl;

import com.epam.digital.data.platform.generator.scope.ReadServiceScope;
import org.springframework.stereotype.Component;
import com.epam.digital.data.platform.generator.factory.AbstractServiceScope;

@Component
public class ReadServiceScopeFactory extends AbstractServiceScope<ReadServiceScope> {

  @Override
  protected ReadServiceScope instantiate() {
    return new ReadServiceScope();
  }

  @Override
  protected String getOperation() {
    return "read";
  }

  @Override
  public String getPath() {
    return "rest-api/src/main/java/restapi/service/read.java.ftl";
  }
}
