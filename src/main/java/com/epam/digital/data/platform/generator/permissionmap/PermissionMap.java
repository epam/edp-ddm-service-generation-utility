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

package com.epam.digital.data.platform.generator.permissionmap;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableMap;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toUnmodifiableList;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;

@Component
public class PermissionMap {

  public static final String IS_AUTHENTICATED = "isAuthenticated()";
  public static final String DENY_ALL = "denyAll";
  public static final String ANY_ROLE = "isAuthenticated";
  public static final String HAS_ANY_ROLE = "hasAnyAuthority('%s')";

  static final String READ = "S";
  static final String CREATE = "I";
  static final String UPDATE = "U";
  static final String DELETE = "D";

  private static final Predicate<Permission> READ_PREDICATE =
      x -> x.getOperation().equals(READ);
  private static final Predicate<Permission> CREATE_PREDICATE =
      x -> x.getOperation().equals(CREATE);
  private static final Predicate<Permission> UPDATE_PREDICATE =
      x -> x.getOperation().equals(UPDATE);
  private static final Predicate<Permission> DELETE_PREDICATE =
      x -> x.getOperation().equals(DELETE);

  private static final Predicate<Permission> COLUMN_NULL_PREDICATE =
      perm -> perm.getColumnName() == null;
  private static final Function<String, Predicate<Permission>> COLUMN_NULL_OR_SAME_NAME_PREDICATE =
      column -> COLUMN_NULL_PREDICATE.or(perm -> perm.getColumnName().equals(column));

  private final Map<String, List<Permission>> rows;

  public PermissionMap(PermissionRepository repository) {
    var map = repository.findAll().stream()
        .collect(groupingBy(Permission::getTableName, mapping(identity(), toUnmodifiableList())));

    this.rows = unmodifiableMap(map);
  }

  public List<String> getReadExpressionsFor(String tableName) {
    return filterExpressions(tableName,
        READ_PREDICATE.and(COLUMN_NULL_PREDICATE))
        .collect(toList());
  }

  public List<String> getUpdateExpressionsFor(String tableName) {
    return filterExpressions(tableName,
        UPDATE_PREDICATE.and(COLUMN_NULL_PREDICATE))
        .collect(toList());
  }

  public List<String> getReadExpressionsFor(String tableName, String columnName) {
    return filterExpressions(tableName,
        READ_PREDICATE.and(COLUMN_NULL_OR_SAME_NAME_PREDICATE.apply(columnName)))
        .collect(toList());
  }

  public List<String> getUpdateExpressionsFor(String tableName, String columnName) {
    return filterExpressions(tableName,
        UPDATE_PREDICATE.and(COLUMN_NULL_OR_SAME_NAME_PREDICATE.apply(columnName)))
        .collect(toList());
  }

  public List<String> getCreateExpressionsFor(String tableName) {
    return filterExpressions(tableName,
        CREATE_PREDICATE)
        .collect(toList());
  }

  public List<String> getDeleteExpressionsFor(String tableName) {
    return filterExpressions(tableName,
        DELETE_PREDICATE)
        .collect(toList());
  }

  private Stream<String> filterExpressions(String tableName, Predicate<Permission> predicate) {
    Optional<List<Permission>> opt = Optional.ofNullable(rows.get(tableName));

    return opt
        .map(list -> filteredExpressionsOrDenyAll(predicate, list))
        .orElse(emptyList())
        .stream();
  }

  private List<String> filteredExpressionsOrDenyAll(
      Predicate<Permission> predicate,
      List<Permission> list) {

    var filtered = list.stream()
        .filter(predicate)
        .map(Permission::getRole)
        .sorted()
        .collect(toList());

    return toCompositeExpression(filtered);
  }

  public List<String> toCompositeExpression(List<String> roles) {
    String expression = DENY_ALL;

    if (roles.contains(ANY_ROLE)) {
      expression = IS_AUTHENTICATED;
    } else if (!roles.isEmpty()) {
      var compositeRole = String.join("','", roles);
      expression = String.format(HAS_ANY_ROLE, compositeRole);
    }
    return List.of(expression);
  }
}
