/*
 * Copyright 2021 EPAM Systems.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.epam.digital.data.platform.generator;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.epam.digital.data.platform.generator.factory.DefaultScopeFactory;
import com.epam.digital.data.platform.generator.factory.Scope;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.scope.ClassScope;
import com.epam.digital.data.platform.generator.visitor.TemplateVisitor;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ServiceGenerationUtilityApplication implements ApplicationRunner {

  private static final String JAVA_EXTENSION = ".java";
  private static final String TEMPLATE_EXTENSION = ".ftl";

  private final DefaultScopeFactory scopeFactory;
  private final Configuration freemarker;
  private final Context context;
  private final Path rootTemplate;

  public ServiceGenerationUtilityApplication(
      DefaultScopeFactory scopeFactory,
      Configuration freemarker, Context context,
      @Qualifier("rootTemplates") Path rootTemplate) {
    this.scopeFactory = scopeFactory;
    this.context = context;
    this.rootTemplate = rootTemplate;
    this.freemarker = freemarker;
  }

  public static void main(String[] args) {
    SpringApplication.run(ServiceGenerationUtilityApplication.class, args);
  }

  @Override
  @SuppressWarnings("rawtypes")
  public void run(ApplicationArguments args) throws Exception {
    List<String> modules = args.getOptionValues("module");

    TemplateVisitor templatePaths = new TemplateVisitor(rootTemplate, modules);
    Files.walkFileTree(rootTemplate, templatePaths);
    List<String> templates = templatePaths.getTemplates();

    for (String templateName : templates) {
      Scope scope = scopeFactory.create(templateName);

      if (scope != null) {
        for (Object scopeItem : scope.create(context)) {
          renderTemplate(templateName, scopeItem);
        }
      } else {
        renderTemplate(templateName, null);
      }
    }
  }

  private void renderTemplate(String templateName, Object model)
      throws IOException, TemplateException {
    Template template = freemarker.getTemplate(templateName);
    try(Writer writer = outputFileWriter(templateName, model)) {
      template.process(model, writer);
    }
  }

  private Writer outputFileWriter(String temp, Object model) throws IOException {
    Writer sourceFileWriter;

    String sourceDir = File.separatorChar + "java";
    String resultFile = temp.replace(TEMPLATE_EXTENSION, "");
    if (temp.contains(sourceDir)) {
      String basePackageName =
          context.getSettings().getGeneral().getBasePackageName().replace('.', File.separatorChar);
      String packageDir = File.separatorChar + basePackageName;
      String[] paths = resultFile.split(safeRegexp(sourceDir));
      String completePath = paths[0] + sourceDir + packageDir + paths[1];

      String path = FilenameUtils.getPath(completePath);
      String baseName = FilenameUtils.getBaseName(completePath);

      if (Character.isLowerCase(baseName.codePointAt(0)) && model != null) {
        baseName = ((ClassScope) model).getClassName();
      }

      File dir = new File(path);
      dir.mkdirs();
      sourceFileWriter = new FileWriter(new File(dir, baseName + JAVA_EXTENSION), UTF_8);
    } else {
      String path = FilenameUtils.getPath(resultFile);
      File dir = new File(path);
      dir.mkdirs();
      sourceFileWriter = new FileWriter(resultFile, UTF_8);
    }
    return sourceFileWriter;
  }

  private String safeRegexp(String sourceDir) {
    return sourceDir.replace("\\", "\\\\");
  }
}
