package ${basePackage}.bpwebservice.soap.wsendpoint;

<#list endpointParameters as endpointParam>
import ${basePackage}.bpwebservice.soap.dto.${endpointParam.requestClassName};
import ${basePackage}.bpwebservice.soap.dto.${endpointParam.responseClassName};
</#list>
import ${basePackage}.bpwebservice.soap.dto.${defaultEndpointParameter.requestClassName};
import ${basePackage}.bpwebservice.soap.dto.${defaultEndpointParameter.responseClassName};
import ${basePackage}.bpwebservice.soap.factory.XmlObjectFactory;
import com.epam.digital.data.platform.bpwebservice.constant.Constants;
import com.epam.digital.data.platform.bpwebservice.dto.BusinessProcessStartDataRequest;
import com.epam.digital.data.platform.bpwebservice.service.StartBpService;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.xml.bind.JAXBElement;
import lombok.RequiredArgsConstructor;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
@RequiredArgsConstructor
public class SoapEndpointHandler {

  private final XmlObjectFactory objectFactory;
  private final StartBpService service;

<#list endpointParameters as endpointParam>
  @PayloadRoot(namespace = Constants.NAMESPACE, localPart = ${endpointParam.requestClassName}.XML_TYPE_NAME)
  @ResponsePayload
  public JAXBElement<${endpointParam.responseClassName}> start${endpointParam.methodName}(@RequestPayload ${endpointParam.requestClassName} request) {
    Map<String, Object> startVars = Objects.isNull(request.getStartVariables()) ? new HashMap<>()
        : Collections.unmodifiableMap(request.getStartVariables());
    var startBpDto = BusinessProcessStartDataRequest.builder()
        .businessProcessDefinitionKey("${endpointParam.processDefinitionId}")
        .startVariables(startVars)
        .build();
    var result = service.startBp(startBpDto);
    var responseDto = new ${endpointParam.responseClassName}();
    responseDto.setResultVariables(result.getResultVariables());
    return objectFactory.create${endpointParam.responseClassName}(responseDto);
  }

 </#list>

   @PayloadRoot(namespace = Constants.NAMESPACE, localPart = ${defaultEndpointParameter.requestClassName}.XML_TYPE_NAME)
   @ResponsePayload
   public JAXBElement<${defaultEndpointParameter.responseClassName}> start${defaultEndpointParameter.methodName}(@RequestPayload ${defaultEndpointParameter.requestClassName} request) {
     Map<String, Object> startVars = Objects.isNull(request.getStartVariables()) ? new HashMap<>()
         : Collections.unmodifiableMap(request.getStartVariables());
     var startBpDto = BusinessProcessStartDataRequest.builder()
         .businessProcessDefinitionKey(request.getBusinessProcessDefinitionKey())
         .startVariables(startVars)
         .build();
     var result = service.startBp(startBpDto);
     var responseDto = new ${defaultEndpointParameter.responseClassName}();
     responseDto.setResultVariables(result.getResultVariables());
     return objectFactory.create${defaultEndpointParameter.responseClassName}(responseDto);
   }
}