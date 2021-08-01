package com.epam.digital.data.platform.generator.permissionmap;

import static org.assertj.core.api.Assertions.assertThat;

import com.epam.digital.data.platform.generator.config.DataSourceEnable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@DataSourceEnable
@SpringBootTest(classes = PermissionMap.class)
class PermissionMapIT {

  @Autowired
  PermissionMap instance;

  @Test
  void shouldFindReads() {
    var roles = instance.getCreateExpressionsFor("pd_processing_consent");

    assertThat(roles).containsExactlyInAnyOrder("denyAll");
  }
}
