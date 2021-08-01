package com.epam.digital.data.platform.generator.factory.impl;

import com.epam.digital.data.platform.generator.scope.UpdateServiceScope;
import org.springframework.stereotype.Component;
import com.epam.digital.data.platform.generator.factory.AbstractServiceScope;

@Component
public class UpdateServiceScopeFactory extends AbstractServiceScope<UpdateServiceScope> {

  @Override
  protected UpdateServiceScope instantiate() {
    return new UpdateServiceScope();
  }

  @Override
  protected String getOperation() {
    return "update";
  }

  @Override
  public String getPath() {
    return "rest-api/src/main/java/restapi/service/update.java.ftl";
  }
}
