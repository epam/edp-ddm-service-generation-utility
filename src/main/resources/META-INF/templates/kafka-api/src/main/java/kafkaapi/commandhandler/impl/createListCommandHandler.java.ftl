package ${basePackage}.kafkaapi.commandhandler.impl;

import com.epam.digital.data.platform.kafkaapi.core.commandhandler.CreateCommandHandler;
import com.epam.digital.data.platform.model.core.kafka.EntityId;
import com.epam.digital.data.platform.model.core.kafka.Request;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ${basePackage}.model.dto.${schemaName};
import ${basePackage}.model.dto.${schemaName}CreateList;

import java.util.ArrayList;
import java.util.List;

@Service
public class ${className} implements CreateCommandHandler<${schemaName}CreateList, List<EntityId>> {

  private final ${schemaName}CreateCommandHandler commandHandler;

  public ${className}(
      ${schemaName}CreateCommandHandler commandHandler) {
    this.commandHandler = commandHandler;
  }

  @Override
  @Transactional
  public List<EntityId> save(Request<${schemaName}CreateList> request) {
    var requestContext = request.getRequestContext();
    var securityContext = request.getSecurityContext();
    var requestPayload = request.getPayload();
    List<EntityId> responseList = new ArrayList<>();
    for (${schemaName} entity : requestPayload.getEntities()) {
      var savedId = commandHandler.save(new Request<>(entity, requestContext, securityContext));
      responseList.add(savedId);
    }
    return responseList;
  }
}

