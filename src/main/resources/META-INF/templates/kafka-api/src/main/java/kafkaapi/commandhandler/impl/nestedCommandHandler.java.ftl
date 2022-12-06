package ${basePackage}.kafkaapi.commandhandler.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.epam.digital.data.platform.kafkaapi.core.commandhandler.UpsertCommandHandler;
import com.epam.digital.data.platform.model.core.kafka.EntityId;
import com.epam.digital.data.platform.model.core.kafka.Request;
import ${basePackage}.model.dto.${schemaName};
import ${basePackage}.model.dto.${rootEntityName};

@Service
public class ${className} implements UpsertCommandHandler<${schemaName}> {

  private final ${rootHandler?cap_first} ${rootHandler};
  <#list nestedHandlers as handler>
  private final ${handler.name?cap_first} ${handler.name};
  </#list>

  public ${className} (
      ${rootHandler?cap_first} ${rootHandler},
  <#list nestedHandlers as handler>
      ${handler.name?cap_first} ${handler.name}<#sep>,
  </#list>
  ) {
    this.${rootHandler} = ${rootHandler};
  <#list nestedHandlers as handler>
    this.${handler.name} = ${handler.name};
  </#list>
  }

  @Override
  @Transactional
  public EntityId upsert(Request<${schemaName}> request) {
    var requestContext = request.getRequestContext();
    var securityContext = request.getSecurityContext();
    var requestPayload = request.getPayload();
    var upsertModel = new ${rootEntityName}();
    <#list simpleFields as field>
    upsertModel.set${field?cap_first}(requestPayload.get${field?cap_first}());
    </#list>
    <#list nestedHandlers as handler>
    var ${handler.injectionField} =
        ${handler.name}
          .upsert(new Request<>(requestPayload.get${handler.childField?cap_first}(), requestContext, securityContext))
          .getId();    
    upsertModel.set${handler.injectionField?cap_first}(${handler.injectionField});
    </#list>
    return ${rootHandler}.upsert(new Request<>(upsertModel, requestContext, securityContext));
  }
}
