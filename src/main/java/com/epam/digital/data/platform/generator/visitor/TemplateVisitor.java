package com.epam.digital.data.platform.generator.visitor;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

public class TemplateVisitor extends SimpleFileVisitor<Path> {

  private final Path root;
  private final List<String> templates;
  private final List<String> modules;

  public TemplateVisitor(Path root, List<String> modules) {
    this.root = root;
    this.templates = new ArrayList<>();
    this.modules = modules;
  }

  @Override
  public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
    if (modules.stream().map(s -> root.toString() + File.separatorChar + s).anyMatch(dir::startsWith) ||
        dir.equals(root)) {
      return FileVisitResult.CONTINUE;
    }
    return FileVisitResult.SKIP_SUBTREE;
  }

  @Override
  public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
    Path relativize = root.relativize(file);
    String extension = FilenameUtils.getExtension(file.toString());
    if ("ftl".equals(extension)) {
      templates.add(relativize.toString());
    } else {
      FileUtils.copyURLToFile(file.toUri().toURL(), new File(relativize.toString()));
    }
    return FileVisitResult.CONTINUE;
  }

  public List<String> getTemplates() {
    return templates;
  }

}
