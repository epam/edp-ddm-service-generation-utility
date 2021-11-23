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

import static com.epam.digital.data.platform.generator.permissionmap.PermissionMap.ANY_ROLE;
import static com.epam.digital.data.platform.generator.permissionmap.PermissionMap.CREATE;
import static com.epam.digital.data.platform.generator.permissionmap.PermissionMap.DELETE;
import static com.epam.digital.data.platform.generator.permissionmap.PermissionMap.DENY_ALL;
import static com.epam.digital.data.platform.generator.permissionmap.PermissionMap.IS_AUTHENTICATED;
import static com.epam.digital.data.platform.generator.permissionmap.PermissionMap.READ;
import static com.epam.digital.data.platform.generator.permissionmap.PermissionMap.UPDATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PermissionMapTest {

  static final String SOME_TABLE_NAME = "SOME_TABLE_NAME";
  static final String NULL_COLUMN_TABLE_NAME = "NULL_COLUMN_TABLE_NAME";
  static final String IGNORING_COLUMN_TABLE_NAME = "IGNORING_COLUMN_TABLE_NAME";
  static final String NO_ROLE_FOR_COLUMN_TABLE_NAME = "NO_ROLE_TABLE_NAME";
  static final String NO_ACCESS_MAP_TABLE_NAME = "NO_ACCESS_MAP_TABLE_NAME";
  static final String ANY_FOR_ANY_TABLE_NAME = "ANY_FOR_ANY_TABLE_NAME";
  static final String COMPOSITE_ROLE_TABLE_NAME = "COMPOSITE_ROLE_TABLE_NAME";
  static final String ONE_ROLE_ACCESS_TABLE_NAME = "ONE_ROLE_ACCESS_TABLE_NAME";

  static final String SOME_COLUMN_NAME = "SOME_COLUMN_NAME";
  static final String JUST_ONE_MORE_COLUMN_NAME = "JUST_ONE_MORE_COLUMN_NAME";
  static final String DENIED_READ_COLUMN_NAME = "DENIED_READ_COLUMN_NAME";
  static final String ANY_ROLE_READ_COLUMN_NAME = "ANY_ROLE_READ_COLUMN_NAME";

  static final String CITIZEN_ROLE_TITLE = "citizen";
  static final String OFFICER_ROLE_TITLE = "officer";
  static final String MAJOR_OFFICER_ROLE_TITLE = "major-officer";

  static final String CITIZEN_ROLE_EXPRESSION =
      "hasAnyAuthority('" + CITIZEN_ROLE_TITLE + "')";
  static final String MAJOR_OFFICER_ROLE_EXPRESSION =
      "hasAnyAuthority('" + MAJOR_OFFICER_ROLE_TITLE + "')";
  static final String COMPOSITE_CITIZEN_AND_OFFICER_ROLE_EXPRESSION =
      "hasAnyAuthority('" + CITIZEN_ROLE_TITLE + "','" + OFFICER_ROLE_TITLE + "')";
  static final String COMPOSITE_MAJOR_OFFICER_AND_OFFICER_ROLE_EXPRESSION =
      "hasAnyAuthority('" + MAJOR_OFFICER_ROLE_TITLE + "','" + OFFICER_ROLE_TITLE + "')";

  @Mock
  PermissionRepository repository;

  PermissionMap instance;

  @BeforeEach
  void setup() {
    setupRbac(generatePerms());
  }

  private void setupRbac(List<Permission> list) {
    given(repository.findAll()).willReturn(list);
    instance = new PermissionMap(repository);
  }

  private List<Permission> generatePerms() {
    return List.of(
        new Permission(1L, SOME_TABLE_NAME, SOME_COLUMN_NAME, READ, CITIZEN_ROLE_TITLE),
        new Permission(1L, SOME_TABLE_NAME, SOME_COLUMN_NAME, READ, OFFICER_ROLE_TITLE),
        new Permission(1L, SOME_TABLE_NAME, JUST_ONE_MORE_COLUMN_NAME, READ, CITIZEN_ROLE_TITLE),

        new Permission(1L, SOME_TABLE_NAME, SOME_COLUMN_NAME, UPDATE, OFFICER_ROLE_TITLE),
        new Permission(1L, SOME_TABLE_NAME, SOME_COLUMN_NAME, UPDATE, MAJOR_OFFICER_ROLE_TITLE),
        new Permission(1L, SOME_TABLE_NAME, JUST_ONE_MORE_COLUMN_NAME, UPDATE, MAJOR_OFFICER_ROLE_TITLE),

        new Permission(1L, SOME_TABLE_NAME, null, CREATE, MAJOR_OFFICER_ROLE_TITLE),

        new Permission(1L, SOME_TABLE_NAME, null, DELETE, MAJOR_OFFICER_ROLE_TITLE),

        new Permission(1L, IGNORING_COLUMN_TABLE_NAME, SOME_COLUMN_NAME, CREATE, MAJOR_OFFICER_ROLE_TITLE),
        new Permission(1L, IGNORING_COLUMN_TABLE_NAME, SOME_COLUMN_NAME, DELETE, MAJOR_OFFICER_ROLE_TITLE),

        new Permission(1L, NULL_COLUMN_TABLE_NAME, null, READ, CITIZEN_ROLE_TITLE),
        new Permission(1L, NULL_COLUMN_TABLE_NAME, null, UPDATE, MAJOR_OFFICER_ROLE_TITLE),

        new Permission(1L, SOME_TABLE_NAME, ANY_ROLE_READ_COLUMN_NAME, READ, ANY_ROLE),
        new Permission(1L, SOME_TABLE_NAME, ANY_ROLE_READ_COLUMN_NAME, UPDATE, ANY_ROLE),

        new Permission(1L, ANY_FOR_ANY_TABLE_NAME, null, READ, ANY_ROLE),
        new Permission(1L, ANY_FOR_ANY_TABLE_NAME, null, UPDATE, ANY_ROLE),

        new Permission(1L, NO_ROLE_FOR_COLUMN_TABLE_NAME, "whatever", READ, "whatever"),

        new Permission(1L, COMPOSITE_ROLE_TABLE_NAME, SOME_COLUMN_NAME, READ, CITIZEN_ROLE_TITLE),
        new Permission(1L, COMPOSITE_ROLE_TABLE_NAME, SOME_COLUMN_NAME, READ, OFFICER_ROLE_TITLE),
        new Permission(1L, COMPOSITE_ROLE_TABLE_NAME, SOME_COLUMN_NAME, READ, ANY_ROLE),

        new Permission(1L, ONE_ROLE_ACCESS_TABLE_NAME, null, READ, CITIZEN_ROLE_TITLE),
        new Permission(1L, ONE_ROLE_ACCESS_TABLE_NAME, SOME_COLUMN_NAME, READ, OFFICER_ROLE_TITLE),
        new Permission(1L, ONE_ROLE_ACCESS_TABLE_NAME, null, UPDATE, CITIZEN_ROLE_TITLE),
        new Permission(1L, ONE_ROLE_ACCESS_TABLE_NAME, SOME_COLUMN_NAME, UPDATE, OFFICER_ROLE_TITLE)
    );
  }

  @Nested
  class Read {

    @Nested
    class PermitOrDeny {

      @Test
      void shouldPermitAllWhenNoSuchTableInPermissionMap() {
        assertThat(instance.getReadExpressionsFor(NO_ACCESS_MAP_TABLE_NAME)).isEmpty();
      }

      @Test
      void shouldPermitForAuthenticatedWhenTableWithoutColumnsForAnyRole() {
        assertThat(instance.getReadExpressionsFor(ANY_FOR_ANY_TABLE_NAME)).containsExactly(IS_AUTHENTICATED);
      }

      @Test
      void shouldDenyAllWhenTableInPermissionMap() {
        assertThat(instance.getReadExpressionsFor(SOME_TABLE_NAME)).containsExactly(DENY_ALL);
      }

      @Test
      void shouldPermitOnlyForCitizenRole() {
        assertThat(instance.getReadExpressionsFor(ONE_ROLE_ACCESS_TABLE_NAME)).containsExactly(CITIZEN_ROLE_EXPRESSION);
      }
    }

    @Nested
    class ReadExpressions {

      @Test
      void shouldReturnNoExpressionsWhenNoSuchTableInPermissionsMap() {
        assertThat(instance.getReadExpressionsFor(NO_ACCESS_MAP_TABLE_NAME, SOME_COLUMN_NAME))
            .isEmpty();
      }

      @Test
      void shouldReturnDenyAllWhenNoSuchColumnInPermissionsMap() {
        assertThat(instance.getReadExpressionsFor(SOME_TABLE_NAME, DENIED_READ_COLUMN_NAME))
            .containsExactly(DENY_ALL);
      }

      @Test
      void shouldFindReadExpressionsByColumnName() {
        assertThat(instance.getReadExpressionsFor(SOME_TABLE_NAME, SOME_COLUMN_NAME))
            .containsExactly(COMPOSITE_CITIZEN_AND_OFFICER_ROLE_EXPRESSION);
      }

      @Test
      void shouldFindReadExpressionsByAnyColumnNameWhenAllColumnsAllowed() {
        assertThat(instance.getReadExpressionsFor(NULL_COLUMN_TABLE_NAME, SOME_COLUMN_NAME))
            .containsExactly(CITIZEN_ROLE_EXPRESSION);
        assertThat(instance.getReadExpressionsFor(NULL_COLUMN_TABLE_NAME, null))
            .containsExactly(CITIZEN_ROLE_EXPRESSION);
      }

      @Test
      void shouldFindAllReadExpressionsByColumnNameWhenAnyRolePermission() {
        assertThat(instance.getReadExpressionsFor(SOME_TABLE_NAME, ANY_ROLE_READ_COLUMN_NAME))
            .containsExactly(IS_AUTHENTICATED);
      }

      @Test
      void shouldReturnOnlyIsAuthenticatedExpressionWhenIsAuthenticatedPresent() {
        assertThat(instance.getReadExpressionsFor(COMPOSITE_ROLE_TABLE_NAME, SOME_COLUMN_NAME))
            .containsExactly(IS_AUTHENTICATED);
      }
    }
  }

  @Nested
  class Update {

    @Nested
    class PermitOrDeny {

      @Test
      void shouldPermitAllWhenNoSuchTableInPermissionMap() {
        assertThat(instance.getUpdateExpressionsFor(NO_ACCESS_MAP_TABLE_NAME)).isEmpty();
      }

      @Test
      void shouldPermitForAuthenticatedWhenTableWithoutColumnsForAnyRole() {
        assertThat(instance.getUpdateExpressionsFor(ANY_FOR_ANY_TABLE_NAME)).containsExactly(IS_AUTHENTICATED);
      }

      @Test
      void shouldDenyAllWhenTableInPermissionMap() {
        assertThat(instance.getUpdateExpressionsFor(SOME_TABLE_NAME)).containsExactly(DENY_ALL);
      }

      @Test
      void shouldPermitOnlyForCitizenRole() {
        assertThat(instance.getUpdateExpressionsFor(ONE_ROLE_ACCESS_TABLE_NAME)).containsExactly(CITIZEN_ROLE_EXPRESSION);
      }
    }

    @Nested
    class UpdateExpressions {

      @Test
      void shouldReturnNoExpressionsWhenNoSuchTableInPermissionsMap() {
        assertThat(instance.getUpdateExpressionsFor(NO_ACCESS_MAP_TABLE_NAME, SOME_COLUMN_NAME))
            .isEmpty();
      }

      @Test
      void shouldReturnDenyAllWhenNoSuchColumnInPermissionsMap() {
        assertThat(instance.getUpdateExpressionsFor(SOME_TABLE_NAME, DENIED_READ_COLUMN_NAME))
            .containsExactly(DENY_ALL);
      }

      @Test
      void shouldFindUpdateExpressionsByColumnNameAndReturnCompositeRole() {
        assertThat(instance.getUpdateExpressionsFor(SOME_TABLE_NAME, SOME_COLUMN_NAME))
            .containsExactly(COMPOSITE_MAJOR_OFFICER_AND_OFFICER_ROLE_EXPRESSION);
      }

      @Test
      void shouldFindReadExpressionsByAnyColumnNameWhenAllColumnsAllowed() {
        assertThat(instance.getUpdateExpressionsFor(NULL_COLUMN_TABLE_NAME, SOME_COLUMN_NAME))
            .containsExactly(MAJOR_OFFICER_ROLE_EXPRESSION);
        assertThat(instance.getUpdateExpressionsFor(NULL_COLUMN_TABLE_NAME, null))
            .containsExactly(MAJOR_OFFICER_ROLE_EXPRESSION);
      }

      @Test
      void shouldFindAllReadExpressionsByColumnNameWhenAnyRolePermission() {
        assertThat(instance.getUpdateExpressionsFor(SOME_TABLE_NAME, ANY_ROLE_READ_COLUMN_NAME))
            .containsExactly(IS_AUTHENTICATED);
      }
    }
  }

  @Nested
  class CreateExpressions {

    @Test
    void shouldReturnNoExpressionsWhenNoSuchTableInPermissionsMap() {
      assertThat(instance.getCreateExpressionsFor(NO_ACCESS_MAP_TABLE_NAME)).isEmpty();
    }

    @Test
    void shouldReturnDenyAllWhenNoSuchColumnInPermissionsMap() {
      assertThat(instance.getCreateExpressionsFor(NO_ROLE_FOR_COLUMN_TABLE_NAME))
          .containsExactly(DENY_ALL);
    }

    @Test
    void shouldFindCreateExpressions() {
      assertThat(instance.getCreateExpressionsFor(SOME_TABLE_NAME))
          .containsExactly(MAJOR_OFFICER_ROLE_EXPRESSION);
    }

    @Test
    void shouldFindCreatesIgnoringColumnName() {
      assertThat(instance.getCreateExpressionsFor(IGNORING_COLUMN_TABLE_NAME))
          .containsExactly(MAJOR_OFFICER_ROLE_EXPRESSION);
    }
  }

  @Nested
  class DeleteExpressions {

    @Test
    void shouldReturnNoExpressionsWhenNoSuchTableInPermissionsMap() {
      assertThat(instance.getDeleteExpressionsFor(NO_ACCESS_MAP_TABLE_NAME)).isEmpty();
    }

    @Test
    void shouldReturnDenyAllWhenNoSuchColumnInPermissionsMap() {
      assertThat(instance.getDeleteExpressionsFor(NO_ROLE_FOR_COLUMN_TABLE_NAME))
          .containsExactly(DENY_ALL);
    }

    @Test
    void shouldFindDeleteExpressions() {
      assertThat(instance.getDeleteExpressionsFor(SOME_TABLE_NAME))
          .containsExactly(MAJOR_OFFICER_ROLE_EXPRESSION);
    }

    @Test
    void shouldFindDeleteExpressionsIgnoringColumnName() {
      assertThat(instance.getDeleteExpressionsFor(IGNORING_COLUMN_TABLE_NAME))
          .containsExactly(MAJOR_OFFICER_ROLE_EXPRESSION);
    }
  }
}
