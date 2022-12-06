package ${basePackage}.kafkaapi.commandhandler.impl;

import com.epam.digital.data.platform.kafkaapi.core.commandhandler.CreateCommandHandler;
import com.epam.digital.data.platform.model.core.kafka.EntityId;
import com.epam.digital.data.platform.model.core.kafka.Request;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ${basePackage}.model.dto.${childSchemaName};
import ${basePackage}.model.dto.${listEntitySchemaName};

import java.util.ArrayList;
import java.util.List;

@Service
public class ${className} implements CreateCommandHandler<${listEntitySchemaName}, List<EntityId>> {

  private final ${childCommandHandlerName} commandHandler;

  public ${className}(${childCommandHandlerName} commandHandler) {
    this.commandHandler = commandHandler;
  }

  @Override
  @Transactional
  public List<EntityId> save(Request<${listEntitySchemaName}> request) {
    var requestContext = request.getRequestContext();
    var securityContext = request.getSecurityContext();
    var requestPayload = request.getPayload();
    List<EntityId> responseList = new ArrayList<>();
    for (${childSchemaName} entity : requestPayload.getEntities()) {
      var savedId = commandHandler.save(new Request<>(entity, requestContext, securityContext));
      responseList.add(savedId);
    }
    return responseList;
  }
}

