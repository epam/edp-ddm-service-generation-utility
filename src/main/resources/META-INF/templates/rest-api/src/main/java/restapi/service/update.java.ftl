package ${basePackage}.restapi.service;

import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.stereotype.Service;

import ${basePackage}.model.dto.${schemaName};
import com.epam.digital.data.platform.model.core.kafka.Request;
import com.epam.digital.data.platform.model.core.kafka.Response;
import com.epam.digital.data.platform.restapi.core.exception.ForbiddenOperationException;
import com.epam.digital.data.platform.restapi.core.service.GenericService;
import com.epam.digital.data.platform.restapi.core.service.JwtInfoProvider;
import com.epam.digital.data.platform.starter.kafka.config.properties.KafkaProperties;
import com.epam.digital.data.platform.starter.security.exception.JwtClaimIncorrectAttributeException;
import com.epam.digital.data.platform.starter.security.jwt.JwtClaimsUtils;
import com.fasterxml.jackson.core.type.TypeReference;

@Service
public class ${className} extends GenericService<Request<${schemaName}>, Void> {

  private static final String REQUEST_TYPE = "${requestType}";

  protected final JwtInfoProvider jwtInfoProvider;

  public ${className}(
    ReplyingKafkaTemplate<String, Request<${schemaName}>, String> replyingKafkaTemplate,
      KafkaProperties kafkaProperties, JwtInfoProvider jwtInfoProvider) {

    super(replyingKafkaTemplate, kafkaProperties.getRequestReply().getTopics().get(REQUEST_TYPE));
    this.jwtInfoProvider = jwtInfoProvider;
  }

  @Override
  protected TypeReference type() {
    return new TypeReference<Response<Void>>() {};
  }

  <#if rls??>
  @Override
  public Response<Void> request(Request<${schemaName}> input) {
    try {
      var checkParam = String.valueOf(input.getPayload().get${rls.checkColumn?cap_first}());
      if (JwtClaimsUtils.getAttributeValueAsStringList(jwtInfoProvider.getUserClaims(input), "${rls.jwtAttribute}")
        .stream().noneMatch(checkParam::startsWith)) {
        var claims = jwtInfoProvider.getUserClaims(input);
        throw new ForbiddenOperationException("User <" + claims.getDrfo() + ":" + claims.getEdrpou() +
          "> dont have permissions to execute this operation ");
      }
    } catch (JwtClaimIncorrectAttributeException e) {
      var claims = jwtInfoProvider.getUserClaims(input);
      throw new ForbiddenOperationException("User <" + claims.getDrfo() + ":" + claims.getEdrpou() +
        "> dont have the required security attribute ${rls.jwtAttribute}");
    }
    return super.request(input);
  }
  </#if>
}