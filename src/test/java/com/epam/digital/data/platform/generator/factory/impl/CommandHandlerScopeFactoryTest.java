package com.epam.digital.data.platform.generator.factory.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.*;

import com.epam.digital.data.platform.generator.scope.CommandHandlerScope;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CommandHandlerScopeFactoryTest {

  private CommandHandlerScopeFactory commandHandlerScopeFactory;

  @BeforeEach
  void setup() {
    commandHandlerScopeFactory = new CommandHandlerScopeFactory();
  }

  @Test
  void commandHandlerScopeFactoryTest() {
    List<CommandHandlerScope> resultList = commandHandlerScopeFactory.create(getContext());
    CommandHandlerScope resultScope = resultList.get(0);
    assertEquals(1, resultList.size());
    assertEquals(SCHEMA_NAME + "CommandHandler", resultScope.getClassName());
    assertEquals(SCHEMA_NAME, resultScope.getSchemaName());
    assertEquals(PK_COLUMN_NAME, resultScope.getPkColumnName());
    assertEquals(TABLE_NAME, resultScope.getTableName());
  }
}
