package ${basePackage}.restapi;

import com.epam.digital.data.platform.restapi.core.annotation.HttpRequestContext;
import com.epam.digital.data.platform.restapi.core.annotation.HttpSecurityContext;
import io.swagger.v3.core.util.PrimitiveType;
import org.springdoc.core.SpringDocUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan({
        "${basePackage}.restapi",
        "com.epam.digital.data.platform.restapi.core",
        "com.epam.digital.data.platform.starter.security.jwt"
})
@SpringBootApplication
public class RestApiApplication {

  static {
    SpringDocUtils.getConfig().addAnnotationsToIgnore(HttpRequestContext.class, HttpSecurityContext.class);
    PrimitiveType.enablePartialTime();
  }

  public static void main(String[] args) {
    SpringApplication.run(RestApiApplication.class, args);
  }

}
