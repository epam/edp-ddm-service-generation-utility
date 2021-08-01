package ${basePackage}.restapi.controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import com.epam.digital.data.platform.restapi.core.exception.NotFoundException;
import com.epam.digital.data.platform.restapi.core.model.EnumLabel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/enum")
public class EnumController {

  private static final Map<String, Map<String, EnumLabel>> MAP = new HashMap<>();

  static {
  <#list endpoints as endpoint>
    var map${endpoint?counter} = new HashMap<String, EnumLabel>();
    <#list endpoint.labels as label>
    map${endpoint?counter}.put("${label.code}", new EnumLabel("${label.code}", "${label.label}"));
    </#list>
    MAP.put("${endpoint.endpoint}", map${endpoint?counter});

  </#list>
  }

  <#list endpoints as endpoint>
  @GetMapping("/${endpoint.endpoint}")
  public ResponseEntity<Collection<EnumLabel>> ${endpoint.methodName}All() {
    return ResponseEntity.status(HttpStatus.OK).body(MAP.get("${endpoint.endpoint}").values());
  }

  @GetMapping("/${endpoint.endpoint}/{value}")
  public ResponseEntity<EnumLabel> ${endpoint.methodName}(@PathVariable("value") String value) {
    var label = MAP.get("${endpoint.endpoint}").get(value);

    if (label == null) {
      throw new NotFoundException(value + " is not a part of \"${endpoint.endpoint}\" enum");
    }

    return ResponseEntity.status(HttpStatus.OK).body(label);
  }
  </#list>
}
