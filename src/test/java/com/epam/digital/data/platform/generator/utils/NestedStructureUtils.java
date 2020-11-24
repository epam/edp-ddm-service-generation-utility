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

package com.epam.digital.data.platform.generator.utils;

import com.epam.digital.data.platform.generator.metadata.NestedNode;
import com.epam.digital.data.platform.generator.metadata.NestedStructure;
import schemacrawler.schema.Catalog;

import java.util.Collections;
import java.util.Map;

import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.newCatalog;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withFkColumn;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withTable;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withTextColumn;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withUuidColumn;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.withUuidPk;

public class NestedStructureUtils {

  public static Catalog mockNestedDbCatalog() {
    var itemPkColumn = withUuidPk("id");
    var itemTable = withTable("item", itemPkColumn, withTextColumn("name"));
    var transactionPkColumn = withUuidPk("id");
    var transactionTable = withTable("transaction", transactionPkColumn, withTextColumn("name"),
            withFkColumn("item_id", itemPkColumn.getColumns().get(0)));
    var orderPkColumn = withUuidPk("id");
    var orderTable =
            withTable(
                    "order",
                    orderPkColumn,
                    withTextColumn("name"),
                    withFkColumn("item_id", itemPkColumn.getColumns().get(0)),
                    withFkColumn("transaction_id", transactionPkColumn.getColumns().get(0)));
    var applicationTable =
            withTable(
                    "application",
                    withUuidPk("id"),
                    withTextColumn("name"),
                    withFkColumn("order_id", orderPkColumn.getColumns().get(0)));
    return newCatalog(itemTable, transactionTable, orderTable, applicationTable);
  }

  public static NestedStructure mockSingleChildChainNestedStructure() {
    var nestedStructure = new NestedStructure();
    nestedStructure.setName("nesting_flow");
    nestedStructure.setRoot(
        createNestedNode(
            "application",
            Map.of(
                "order_id",
                createNestedNode(
                    "order",
                    Map.of(
                        "transaction_id",
                        createNestedNode("transaction", Collections.emptyMap()))))));
    return nestedStructure;
  }

  public static NestedStructure mockMultipleChildNodesNestedStructure() {
    var nestedStructure = new NestedStructure();
    nestedStructure.setName("nesting_flow");
    nestedStructure.setRoot(
        createNestedNode(
            "application",
            Map.of(
                "order_id",
                createNestedNode(
                    "order",
                    Map.of(
                        "transaction_id",
                        createNestedNode("transaction", Collections.emptyMap()),
                        "item_id",
                        createNestedNode("item", Collections.emptyMap()))))));
    return nestedStructure;
  }

  public static NestedStructure mockSingleLevelOfDepthNestedStructure() {
    var nestedStructure = new NestedStructure();
    nestedStructure.setName("nesting_flow");
    nestedStructure.setRoot(
        createNestedNode(
            "application", Map.of("order_id", createNestedNode("order", Collections.emptyMap()))));
    return nestedStructure;
  }

  private static NestedNode createNestedNode(
      String tableName, Map<String, NestedNode> childNodes) {
    var node = new NestedNode();
    node.setTableName(tableName);
    node.setChildNodes(childNodes);
    return node;
  }
}
