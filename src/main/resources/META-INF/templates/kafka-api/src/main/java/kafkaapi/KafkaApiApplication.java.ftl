package ${basePackage}.kafkaapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan({"${basePackage}.kafkaapi",
        "com.epam.digital.data.platform.kafkaapi.core"})
@SpringBootApplication
public class KafkaApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(KafkaApiApplication.class, args);
  }

}
