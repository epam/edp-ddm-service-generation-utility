package com.epam.digital.data.platform.generator.config;

import static com.epam.digital.data.platform.generator.config.MainConfigTest.Row.KAFKA;
import static com.epam.digital.data.platform.generator.config.MainConfigTest.Row.READ;
import static com.epam.digital.data.platform.generator.config.MainConfigTest.Row.RETENTION_POLICY;
import static com.epam.digital.data.platform.generator.config.MainConfigTest.Row.WRITE;
import static org.assertj.core.api.Assertions.assertThat;

import com.epam.digital.data.platform.generator.model.Blueprint;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class MainConfigTest {

  private static final int DEFAULT_RETENTION_POLICY = 3 * 365;
  private static final String settingsStr =
      "settings:\n"
          + "  general:\n"
          + "    package: ua.gov.mdtu.ddm.dataplatform.template\n"
          + "    register: registry\n"
          + "    version: 1.2.3\n"
          + "  kafka:\n"
          + "    retention-policy-in-days:\n"
          + "      read: 10\n"
          + "      write: 20";

  private final MainConfig instance = new MainConfig();

  @Test
  void shouldSetDefaultValueForReadAndWriteWhenWholeKafkaBlockIsMissing()
      throws JsonProcessingException {
    var croppedSettings = settingsWithoutRows(KAFKA, RETENTION_POLICY, READ, WRITE);

    assertReadWriteValues(croppedSettings, DEFAULT_RETENTION_POLICY, DEFAULT_RETENTION_POLICY);
  }

  @Test
  void shouldSetDefaultValueForReadAndWriteWhenWholeRetentionPolicyBlockIsMissing()
      throws JsonProcessingException {
    var croppedSettings = settingsWithoutRows(RETENTION_POLICY, READ, WRITE);

    assertReadWriteValues(croppedSettings, DEFAULT_RETENTION_POLICY, DEFAULT_RETENTION_POLICY);
  }

  @Test
  void shouldSetDefaultValueForReadWhenThisRowIsMissing() throws JsonProcessingException {
    var croppedSettings = settingsWithoutRows(READ);

    assertReadWriteValues(croppedSettings, DEFAULT_RETENTION_POLICY, 20);
  }

  @Test
  void shouldSetDefaultValueForWriteWhenThisRowIsMissing() throws JsonProcessingException {
    var croppedSettings = settingsWithoutRows(WRITE);

    assertReadWriteValues(croppedSettings, 10, DEFAULT_RETENTION_POLICY);
  }

  private void assertReadWriteValues(String croppedSettings, int read, int write)
      throws JsonProcessingException {
    var blueprint = instance.yamlMapper().readValue(croppedSettings, Blueprint.class);

    var retentionPolicy = blueprint.getSettings().getKafka().getRetentionPolicyInDays();
    assertThat(retentionPolicy.getRead()).isEqualTo(read);
    assertThat(retentionPolicy.getWrite()).isEqualTo(write);
  }


  enum Row {
    SETTINGS, GENERAL, PACKAGE, REGISTER, VERSION, KAFKA, RETENTION_POLICY, READ, WRITE
  }

  private String settingsWithoutRows(Row... rows) {
    var extraLines = Arrays.stream(rows)
        .map(Row::ordinal)
        .collect(Collectors.toSet());

    var settingsRows = settingsStr.split("\n");
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < settingsRows.length; i++) {
      if (!extraLines.contains(i)) {
        sb.append(settingsRows[i]).append("\n");
      }
    }
    return sb.toString();
  }
}
