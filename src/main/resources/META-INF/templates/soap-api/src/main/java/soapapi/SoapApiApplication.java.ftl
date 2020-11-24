package ${basePackage}.soapapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan({
    "${basePackage}.soapapi",
    "com.epam.digital.data.platform.soapapi.core"
})
@EnableFeignClients
@SpringBootApplication
public class SoapApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(SoapApiApplication.class, args);
  }
}
