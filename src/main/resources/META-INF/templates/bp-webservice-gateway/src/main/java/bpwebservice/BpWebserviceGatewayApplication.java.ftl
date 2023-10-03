package ${basePackage}.bpwebservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.epam.digital.data.platform.bpwebservice",
    "${basePackage}.bpwebservice"})
public class BpWebserviceGatewayApplication {

  public static void main(String[] args) {
    SpringApplication.run(BpWebserviceGatewayApplication.class, args);
  }
}
