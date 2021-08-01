package com.epam.digital.data.platform.generator.factory;

import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.PK_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.SCHEMA_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.getContext;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.epam.digital.data.platform.generator.scope.CreateServiceScope;

class AbstractServiceScopeTest {

  class TestScope extends AbstractServiceScope<CreateServiceScope> {

    @Override
    protected CreateServiceScope instantiate() {
      return new CreateServiceScope();
    }

    @Override
    protected String getOperation() {
      return "create";
    }

    @Override
    public String getPath() {
      return null;
    }
  }

  private TestScope instance;

  @BeforeEach
  void setup() {
    instance = new TestScope();
  }

  @Test
  @DisplayName("AbstractServiceScope successful path")
  void successfulTest() {
    List<CreateServiceScope> resultList = instance.create(getContext());

    assertEquals(1, resultList.size());
    CreateServiceScope resultScope = resultList.get(0);
    assertEquals(SCHEMA_NAME + "CreateService", resultScope.getClassName());
    assertEquals(SCHEMA_NAME, resultScope.getSchemaName());
    assertEquals(PK_NAME, resultScope.getPkName());
    assertEquals(UUID.class.getCanonicalName(), resultScope.getPkType());
  }
}
