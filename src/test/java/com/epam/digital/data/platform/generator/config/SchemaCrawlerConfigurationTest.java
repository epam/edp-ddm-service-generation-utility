package com.epam.digital.data.platform.generator.config;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForColumnInclusion;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForTableInclusion;

import com.epam.digital.data.platform.generator.config.properties.SchemaCrawlerProperties;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion;

class SchemaCrawlerConfigurationTest {

  private SchemaCrawlerConfiguration instance = new SchemaCrawlerConfiguration();

  @Nested
  class LimitOptionsBuilder {

    @Test
    void shouldIgnoreExcludedTableNames() {
      // given
      var survivedTableName = "someschema.survived_table";
      var excludedTableName = "someschema.excluded_table";
      var tables = List.of(excludedTableName, survivedTableName);

      var config = setupAppConfig();
      config.setExcludeTables(List.of(excludedTableName, "someschema.excluded_table_2"));

      // when
      var limitOptionsBuilder = instance.limitOptionsBuilder(config);

      // then
      var survivedTableNames = filterTableNames(tables, limitOptionsBuilder);
      assertEquals(1, survivedTableNames.size());
      assertEquals(survivedTableName, survivedTableNames.get(0));
    }

    @Test
    void shouldIgnoreExcludedTableNamePrefixes() {
      // given
      var survivedTableName = "someschema.survived_table";
      var tables = List.of("someschema.test_excluded_table", survivedTableName);

      var config = setupAppConfig();
      config.setExcludeTablePrefixes(List.of("ddm", "test"));

      // when
      var limitOptionsBuilder = instance.limitOptionsBuilder(config);

      // then
      var survivedTableNames = filterTableNames(tables, limitOptionsBuilder);
      assertEquals(1, survivedTableNames.size());
      assertEquals(survivedTableName, survivedTableNames.get(0));
    }

    @Test
    void shouldIgnoreExcludedFieldNamePrefixes() {
      // given
      var survivedFieldName = "someschema.some_table.survived_field";
      var fields = List.of("someschema.some_table.ddm_excluded_field", survivedFieldName);

      var config = setupAppConfig();
      config.setExcludeFieldPrefixes(List.of("ddm", "test"));

      // when
      var limitOptionsBuilder = instance.limitOptionsBuilder(config);

      // then
      var survivedTableNames = filterFieldNames(fields, limitOptionsBuilder);
      assertEquals(1, survivedTableNames.size());
      assertEquals(survivedFieldName, survivedTableNames.get(0));
    }

    private List<String> filterTableNames(List<String> list,
        schemacrawler.schemacrawler.LimitOptionsBuilder limitOptionsBuilder) {
      return filterNames(list, limitOptionsBuilder, ruleForTableInclusion);
    }

    private List<String> filterFieldNames(List<String> list,
        schemacrawler.schemacrawler.LimitOptionsBuilder limitOptionsBuilder) {
      return filterNames(list, limitOptionsBuilder, ruleForColumnInclusion);
    }

    private List<String> filterNames(List<String> list,
        schemacrawler.schemacrawler.LimitOptionsBuilder limitOptionsBuilder,
        DatabaseObjectRuleForInclusion rule) {
      var inclusionRule = limitOptionsBuilder.toOptions().get(rule);
      return list.stream().filter(inclusionRule).collect(toList());
    }

  }

  private SchemaCrawlerProperties setupAppConfig() {
    var config = new SchemaCrawlerProperties();
    config.setSchema("someschema");
    config.setExcludeTables(emptyList());
    config.setExcludeTablePrefixes(emptyList());
    return config;
  }
}
