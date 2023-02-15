package ${basePackage}.restapi.service;

import com.epam.digital.data.platform.model.core.search.SearchConditionPage;
import com.epam.digital.data.platform.restapi.core.service.GenericSearchService;
import org.springframework.stereotype.Service;
import ${basePackage}.model.dto.${schemaName}SearchConditionResponse;
import ${basePackage}.model.dto.${schemaName}SearchConditions;
import ${basePackage}.restapi.handler.${schemaName}SearchHandler;

@Service
public class ${className}
    extends GenericSearchService<
            ${schemaName}SearchConditions,
            ${schemaName}SearchConditionResponse,
            ${responseType}<${schemaName}SearchConditionResponse>> {


  public ${schemaName}SearchService(
      ${schemaName}SearchHandler handler) {
    super(handler);
  }

  @Override
  protected ${responseType}<${schemaName}SearchConditionResponse> getResponsePayload(
      SearchConditionPage<${schemaName}SearchConditionResponse> searchConditionPage) {
    return searchConditionPage<#if responseAsPlainContent>.getContent()</#if>;
  }
}
