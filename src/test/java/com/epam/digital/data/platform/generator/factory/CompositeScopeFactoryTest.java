package com.epam.digital.data.platform.generator.factory;

import static org.assertj.core.api.Assertions.assertThat;

import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.utils.PathConverter;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CompositeScopeFactoryTest {

  private static final String CORRECT_PATH = PathConverter.safePath("ua/gov/Main.class");

  private TestScopeFactory testScopeFactory;
  private DefaultScopeFactory defaultScopeFactory;

  private class TestScopeFactory implements ScopeFactory {

    @Override
    public List create(Context context) {
      return Arrays.asList("A");
    }

    @Override
    public String getPath() {
      return CORRECT_PATH;
    }
  }

  @BeforeEach
  void init() {
    testScopeFactory = new TestScopeFactory();
    defaultScopeFactory = new DefaultScopeFactory(List.of(testScopeFactory));
  }

  @Test
  void shouldReturnResultAsCompositeOfResults() {
    defaultScopeFactory = new DefaultScopeFactory(List.of(testScopeFactory, testScopeFactory));

    var resultScope = defaultScopeFactory.create(CORRECT_PATH);
    var resultList = resultScope.create(null);

    assertThat(resultList)
        .hasSize(2)
        .containsOnly("A");
  }
}