package com.epam.digital.data.platform.generator.utils;

import com.google.common.base.CaseFormat;

public class CaseConverter {
  private CaseConverter() {}

  public static String camelToUnderscore(String value) {
    return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, value);
  }
}
