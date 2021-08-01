package com.epam.digital.data.platform.generator.factory;

import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.TABLE_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.VIEW_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.newCatalog;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withSearchConditionView;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withTable;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.permissionmap.PermissionMap;
import java.util.List;
import org.junit.jupiter.api.Test;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Table;

class RoleBasedAbstractScopeTest {

  private static final Object[] EXPECTED = {new Object(), new Object()};

  TestRoleBasedAbstractScope instance = new TestRoleBasedAbstractScope(null);

  @Test
  void shouldReturnScopes() {
    Catalog catalog = newCatalog(
        withTable(TABLE_NAME),
        withTable(TABLE_NAME + "_2"),
        withSearchConditionView(VIEW_NAME));
    Context context = new Context(null, catalog);

    var scopes = instance.create(context);

    assertThat(scopes).containsExactly(EXPECTED);
  }

  private static class TestRoleBasedAbstractScope extends RoleBasedAbstractScope<Object> {

    protected TestRoleBasedAbstractScope(
        PermissionMap permissionMap) {
      super(permissionMap);
    }

    @Override
    protected boolean isApplicable(Table table) {
      return table.getName().equals(TABLE_NAME);
    }

    @Override
    protected List<Object> map(Table table, Context context) {
      return asList(EXPECTED);
    }

    @Override
    public String getPath() {
      return "whatever";
    }
  }
}
