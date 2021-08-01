package com.epam.digital.data.platform.generator.factory.impl;

import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.getContext;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.newCatalog;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withLocalDateTimeColumn;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withTable;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withTextColumn;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withUuidPk;
import static com.epam.digital.data.platform.generator.utils.TestUtils.findByStringContains;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.epam.digital.data.platform.generator.metadata.PartialUpdate;
import com.epam.digital.data.platform.generator.metadata.PartialUpdateProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.scope.CommandHandlerScope;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PartialUpdateCommandHandlerScopeFactoryTest {

  private static final String UPDATE_1 = "my_table_upd";
  private static final String UPDATE_2 = "second_test_table_upd";
  private static final String TABLE_1 = "my_table";
  private static final String TABLE_2 = "second_test_table";

  @Mock
  private PartialUpdateProvider provider;

  private PartialUpdateCommandHandlerScopeFactory instance;

  private Context context = getContext();

  @BeforeEach
  void setup() {
    instance = new PartialUpdateCommandHandlerScopeFactory(provider);

    context = new Context(null,
        newCatalog(
            withTable(TABLE_1, withUuidPk("pk"),
                withTextColumn("col"), withLocalDateTimeColumn("my_col")),
            withTable(TABLE_2, withUuidPk("pk2"),
                withTextColumn("col3"))));

  }

  @Test
  void shouldCreateCommandHandlerScope() {
    given(provider.findAll()).willReturn(List.of(
        new PartialUpdate(UPDATE_1, TABLE_1, Set.of("col", "my_col")),
        new PartialUpdate(UPDATE_2, TABLE_2, Set.of("col3"))
    ));

    var resultList = instance.create(context);

    assertThat(resultList).hasSize(2);

    var resultScope = findByStringContains("MyTable", CommandHandlerScope::getSchemaName, resultList);
    assertThat(resultScope.getClassName()).isEqualTo("MyTableMyTableUpdCommandHandler");
    assertThat(resultScope.getSchemaName()).isEqualTo("MyTableMyTableUpd");
    assertThat(resultScope.getPkColumnName()).isEqualTo("pk");
    assertThat(resultScope.getTableName()).isEqualTo("my_table");
  }
}
