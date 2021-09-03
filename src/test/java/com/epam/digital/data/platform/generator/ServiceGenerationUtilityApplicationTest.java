package com.epam.digital.data.platform.generator;

import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.getContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.epam.digital.data.platform.generator.factory.DefaultScopeFactory;
import com.epam.digital.data.platform.generator.factory.Scope;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.scope.ClassScope;
import freemarker.template.Configuration;
import freemarker.template.Template;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.ApplicationArguments;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ServiceGenerationUtilityApplicationTest {

  private static final String TARGET_FOLDER = "./testModule/";
  private static final String TARGET_JAVA_FOLDER = "./javaTestModule/";

  private static final String ROOT_FOLDER = "src/test/resources/templates/";

  private static final String MODEL_RESULT_URL = "./testModule/template";
  private static final String JAVA_MODEL_RESULT_URL = "./javaTestModule/java/ua/gov/mdtu/ddm/ClassName.java";

  private static final Path rootPath = Paths.get(ROOT_FOLDER);

  private ServiceGenerationUtilityApplication serviceGenerationUtilityApplication;

  @Mock
  private ApplicationArguments args;
  @Mock
  private Configuration freemarker;
  @Mock
  private DefaultScopeFactory scopeFactory;
  @Mock
  private Scope scope;
  @Mock
  private Template template;
  @Captor
  private ArgumentCaptor<Object> capturedModel;

  private final Context context = getContext();

  @BeforeEach
  void init() throws IOException {
    erase();

    serviceGenerationUtilityApplication = new ServiceGenerationUtilityApplication(
        scopeFactory, freemarker, context, rootPath);

    when(scopeFactory.create(anyString())).thenReturn(scope);
    when(freemarker.getTemplate(any())).thenReturn(template);
  }

  @AfterEach
  void erase() throws IOException {
    deleteFolder(TARGET_FOLDER);
    deleteFolder(TARGET_JAVA_FOLDER);
  }

  @Test
  void shouldPutResultFileIntoCurrentFolder() throws Exception {
    when(args.getOptionValues("module")).thenReturn(List.of("testModule"));
    when(scope.create(context)).thenReturn(List.of(new Object()));

    serviceGenerationUtilityApplication.run(args);

    File resultFile = new File(MODEL_RESULT_URL);
    assertThat(resultFile).exists();
  }

  @Test
  void shouldPutResultFileIntoPackageFolderWithClassName() throws Exception {
    when(args.getOptionValues("module")).thenReturn(List.of("javaTestModule"));
    ClassScope model = new ClassScope();
    model.setClassName("ClassName");
    when(scope.create(context)).thenReturn(List.of(model));

    serviceGenerationUtilityApplication.run(args);

    File resultFile = new File(JAVA_MODEL_RESULT_URL);
    assertThat(resultFile).exists();
  }

  @Test
  void shouldSetModelToNullWhenScopeIsNull() throws Exception {
    when(args.getOptionValues("module")).thenReturn(List.of("testModule"));
    when(scopeFactory.create(any(String.class))).thenReturn(null);

    serviceGenerationUtilityApplication.run(args);

    verify(template, times(1)).process(capturedModel.capture(), any());
    List<Object> models = capturedModel.getAllValues();
    assertThat(models).containsOnlyNulls();
  }

  private void deleteFolder(String folder) throws IOException {
    FileUtils.deleteDirectory(new File(folder));
  }
}
