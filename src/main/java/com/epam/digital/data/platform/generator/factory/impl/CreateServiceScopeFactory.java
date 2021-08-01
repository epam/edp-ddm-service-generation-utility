package com.epam.digital.data.platform.generator.factory.impl;

import org.springframework.stereotype.Component;
import com.epam.digital.data.platform.generator.factory.AbstractServiceScope;
import com.epam.digital.data.platform.generator.scope.CreateServiceScope;

@Component
public class CreateServiceScopeFactory extends AbstractServiceScope<CreateServiceScope> {

  @Override
  protected CreateServiceScope instantiate() {
    return new CreateServiceScope();
  }

  @Override
  protected String getOperation() {
    return "create";
  }

  @Override
  public String getPath() {
    return "rest-api/src/main/java/restapi/service/create.java.ftl";
  }
}
