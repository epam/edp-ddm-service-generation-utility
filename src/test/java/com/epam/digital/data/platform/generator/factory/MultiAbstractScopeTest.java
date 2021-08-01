package com.epam.digital.data.platform.generator.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.newCatalog;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withSearchConditionView;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withTable;

import com.epam.digital.data.platform.generator.model.Context;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Table;

class MultiAbstractScopeTest {

  private static final String APPLICABLE_PREFIX = "test_";
  private static final String APPLICABLE_VIEW = APPLICABLE_PREFIX + "view";
  private static final String APPLICABLE_VIEW_NAME = APPLICABLE_VIEW + "_v";

  TestMultiAbstractScope instance;

  Context ctx;

  @BeforeEach
  void setup() {
    instance = new TestMultiAbstractScope();

    Catalog catalog = newCatalog(
        withTable("some_table"),
        withSearchConditionView(APPLICABLE_VIEW));
    ctx = new Context(null, catalog);
  }

  @Test
  void shouldMapOnlyApplicable() {
    var scopes = instance.create(ctx);

    assertThat(scopes).containsOnly(APPLICABLE_VIEW_NAME);
  }

  @Test
  void shouldComposeEndpoint() {
    var endpoint = instance.getEndpoint(APPLICABLE_VIEW_NAME);

    assertThat(endpoint).isEqualTo("/test-view");
  }

  static class TestMultiAbstractScope extends MultiAbstractScope<String> {

    @Override
    protected String map(Table table, Context context) {
      return table.getName();
    }

    @Override
    protected boolean isApplicable(Table table) {
      return table.getName().startsWith(APPLICABLE_PREFIX);
    }

    @Override
    public String getPath() {
      return null;
    }
  }
}
