package com.epam.digital.data.platform.generator.factory;

import static org.assertj.core.api.Assertions.assertThat;

import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.utils.PathConverter;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DefaultScopeFactoryTest {

  private static final String CORRECT_PATH = PathConverter.safePath("ua/gov/Main.class");
  private static final String INCORRECT_PATH = PathConverter.safePath("ua/gov/Some.class");

  private TestScopeFactory testScopeFactory;
  private DefaultScopeFactory defaultScopeFactory;

  private class TestScopeFactory implements ScopeFactory {

    @Override
    public List create(Context context) {
      return null;
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
  void shouldReturnNullWhenIncorrectPath() {
    assertThat(defaultScopeFactory.create(INCORRECT_PATH)).isNull();
  }

  @Test
  void shouldReturnScopeFactoryWhenCorrectPath() {
    assertThat(defaultScopeFactory.create(CORRECT_PATH)).isEqualTo(testScopeFactory);
  }

  @Test
  void shouldReturnCompositeScopeFactory() {
    defaultScopeFactory = new DefaultScopeFactory(List.of(testScopeFactory, testScopeFactory));

    var resultScope = defaultScopeFactory.create(CORRECT_PATH);

    assertThat(resultScope).isInstanceOf(CompositeScopeFactory.class);
  }

}