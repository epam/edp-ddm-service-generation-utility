package com.epam.digital.data.platform.generator.factory.impl;

import com.epam.digital.data.platform.generator.scope.DeleteServiceScope;
import org.springframework.stereotype.Component;
import com.epam.digital.data.platform.generator.factory.AbstractServiceScope;

@Component
public class DeleteServiceScopeFactory extends AbstractServiceScope<DeleteServiceScope> {

  @Override
  protected DeleteServiceScope instantiate() {
    return new DeleteServiceScope();
  }

  @Override
  protected String getOperation() {
    return "delete";
  }

  @Override
  public String getPath() {
    return "rest-api/src/main/java/restapi/service/delete.java.ftl";
  }
}
