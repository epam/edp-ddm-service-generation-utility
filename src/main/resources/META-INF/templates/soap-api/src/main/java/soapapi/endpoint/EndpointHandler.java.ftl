package ${basePackage}.soapapi.endpoint;

import com.epam.digital.data.platform.model.core.file.FileResponseDto;
import com.epam.digital.data.platform.soapapi.core.converter.HeadersProvider;
import com.epam.digital.data.platform.soapapi.core.endpoint.IEndpointHandler;
import com.epam.digital.data.platform.soapapi.core.util.SoapHeaders;
import java.util.Map;
import javax.jws.WebParam;
import javax.jws.WebService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ${basePackage}.soapapi.restclients.RestApiClient;
<#list schemaNames as schema>
import ${basePackage}.model.dto.${schema};
</#list>

@WebService
@Component
public class EndpointHandler implements IEndpointHandler {

  private final Logger log = LoggerFactory.getLogger(EndpointHandler.class);

  private final RestApiClient restApiClient;
  private final HeadersProvider headersProvider;

  public EndpointHandler(RestApiClient restApiClient, HeadersProvider headersProvider) {
    this.restApiClient = restApiClient;
    this.headersProvider = headersProvider;
  }

  <#list searchScopes as scope>
  public ${scope.responseType}<${scope.schemaName}SearchConditionResponse> search${scope.schemaName}(
      @WebParam(name = "searchConditions") ${scope.schemaName}SearchConditions searchConditions,
      @WebParam(header = true, name = "headers") SoapHeaders headers) {
    log.info("search${scope.schemaName} called");
    Map<String, Object> headersMap = headersProvider.createHttpHeaders(headers);
    return restApiClient.search${scope.schemaName}(searchConditions, headersMap);
  }
  </#list>

  <#list fileScopes as scope>
  public FileResponseDto ${scope.methodName}(
      @WebParam(name = "id") ${scope.pkType} id,
      @WebParam(name = "fileId") String fileId,
      @WebParam(header = true, name = "headers") SoapHeaders headers) {
    log.info("${scope.methodName} called");
    Map<String, Object> headersMap = headersProvider.createHttpHeaders(headers);
    return restApiClient.${scope.methodName}(id, fileId, headersMap);
  }
  </#list>
}
