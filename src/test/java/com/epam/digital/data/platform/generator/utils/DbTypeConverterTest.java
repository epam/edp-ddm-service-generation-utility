package com.epam.digital.data.platform.generator.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.mock;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.Column;

class DbTypeConverterTest {

  @Test
  void shouldFindJavaTypes() {
    assertThat(DbTypeConverter.convertToJavaTypeName(String.class, "text")).isEqualTo(String.class.getCanonicalName());
    assertThat(DbTypeConverter.convertToJavaTypeName(String.class, "bpchar")).isEqualTo(String.class.getCanonicalName());
    assertThat(DbTypeConverter.convertToJavaTypeName(Object.class, "uuid")).isEqualTo(UUID.class.getCanonicalName());
  }

  @Test
  void shouldConvertToJavaTypes() {
    assertThat(DbTypeConverter.convertToJavaTypeName(Timestamp.class, "timestamptz"))
        .isEqualTo(LocalDateTime.class.getCanonicalName());
    assertThat(DbTypeConverter.convertToJavaTypeName(Date.class, "date"))
        .isEqualTo(LocalDate.class.getCanonicalName());
    assertThat(DbTypeConverter.convertToJavaTypeName(Time.class, "time without time zone"))
        .isEqualTo(LocalTime.class.getCanonicalName());
  }

  @Test
  void shouldFindDomainTypes() {
    assertThat(DbTypeConverter.convertToJavaTypeName(Object.class, "dn_edrpou"))
        .isEqualTo(String.class.getCanonicalName());

    assertThat(DbTypeConverter.convertToJavaTypeName(Object.class, "dn_passport_num"))
        .isEqualTo(String.class.getCanonicalName());
  }

  @Test
  void shouldFindCustomComplexTypes() {
    assertThat(DbTypeConverter.convertToJavaTypeName(Object.class, "type_file"))
            .isEqualTo("com.epam.digital.data.platform.model.core.kafka.File");
  }

  @Test
  void shouldFindClassByStringName() {
    assertThat(DbTypeConverter.convertToJavaTypeName("java.sql.Timestamp", "timestamptz"))
            .isEqualTo(LocalDateTime.class.getCanonicalName());
  }

  @Test
  void shouldFailIfNonExistingClassName() {
    assertThrows(
        IllegalStateException.class,
        () -> DbTypeConverter.convertToJavaTypeName("com.epam.Something", "timestamptz"));
  }

  @Test
  void shouldAcceptColumn() {
    var c = mockColumn(String.class, "text");

    assertThat(DbTypeConverter.convertToJavaTypeName(c)).isEqualTo(String.class.getCanonicalName());
  }

  private Column mockColumn(Class<String> clazz, String dbType) {
    var c = mock(Column.class);
    var t = mock(ColumnDataType.class);
    given(c.getColumnDataType()).willReturn(t);

    willReturn(clazz).given(t).getTypeMappedClass();
    given(t.getName()).willReturn(dbType);
    return c;
  }
}
