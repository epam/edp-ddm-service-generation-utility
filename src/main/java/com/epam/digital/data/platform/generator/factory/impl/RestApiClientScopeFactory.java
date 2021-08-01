package com.epam.digital.data.platform.generator.factory.impl;

import com.epam.digital.data.platform.generator.factory.AbstractSoapScopeFactory;
import com.epam.digital.data.platform.generator.metadata.SearchConditionProvider;
import org.springframework.stereotype.Component;

@Component
public class RestApiClientScopeFactory extends AbstractSoapScopeFactory {

  public RestApiClientScopeFactory(SearchConditionProvider searchConditionProvider) {
    super(searchConditionProvider);
  }

  @Override
  public String getPath() {
    return "soap-api/src/main/java/soapapi/restclients/RestApiClient.java.ftl";
  }
}
