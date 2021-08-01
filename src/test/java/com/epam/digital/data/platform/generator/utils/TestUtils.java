package com.epam.digital.data.platform.generator.utils;

import java.util.List;
import java.util.function.Function;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;

public class TestUtils {

  private TestUtils() {}

  public static final RecursiveComparisonConfiguration ignoringCollectionOrder =
      new RecursiveComparisonConfiguration();

  static {
    ignoringCollectionOrder.ignoreCollectionOrder(true);
  }

  public static <T> T findByStringContains(String str, Function<T, String> func, List<T> list) {
    return list.stream()
        .filter(x -> func.apply(x).contains(str))
        .findAny()
        .orElseThrow(() ->
            new AssertionError(String.format("Can not find entity contains %s in %s", str, list)));
  }
}
