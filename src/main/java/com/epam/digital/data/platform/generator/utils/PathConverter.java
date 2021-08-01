package com.epam.digital.data.platform.generator.utils;

import java.nio.file.Paths;

public class PathConverter {

  private PathConverter() {
  }

  public static String safePath(String path) {
    return Paths.get(path).toString();
  }
}
