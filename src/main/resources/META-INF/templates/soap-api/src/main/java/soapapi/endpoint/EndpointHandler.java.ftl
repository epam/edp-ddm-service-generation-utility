package ${basePackage}.soapapi.endpoint;

<#list schemaNames as schema>
import ${basePackage}.model.dto.${schema};
</#list>

import com.epam.digital.data.platform.soapapi.core.converter.HeadersProvider;
import com.epam.digital.data.platform.soapapi.core.endpoint.IEndpointHandler;
import com.epam.digital.data.platform.soapapi.core.util.SoapHeaders;
import java.util.List;
import java.util.Map;
import javax.jws.WebParam;
import javax.jws.WebService;
import org.springframework.stereotype.Component;
import ${basePackage}.soapapi.restclients.RestApiClient;

@WebService
@Component
public class EndpointHandler implements IEndpointHandler {

  private final RestApiClient restApiClient;
  private final HeadersProvider headersProvider;

  public EndpointHandler(RestApiClient restApiClient, HeadersProvider headersProvider) {
    this.restApiClient = restApiClient;
    this.headersProvider = headersProvider;
  }

<#list searchScopes as scope>
  public List<${scope.schemaName}> search${scope.schemaName}(
      @WebParam(name = "searchConditions") ${scope.schemaName}SearchConditions searchConditions,
      @WebParam(header = true, name = "headers") SoapHeaders headers) {
    Map<String, Object> headersMap = headersProvider.createHttpHeaders(headers);
    return restApiClient.search${scope.schemaName}(searchConditions, headersMap);
  }

</#list>
}
