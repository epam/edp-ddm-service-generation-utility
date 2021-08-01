package com.epam.digital.data.platform.generator.factory.impl;

import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.ENDPOINT;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.PK_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.SCHEMA_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.TABLE_NAME;
import static com.epam.digital.data.platform.generator.utils.ContextTestUtils.getContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.permissionmap.PermissionMap;
import com.epam.digital.data.platform.generator.scope.ControllerScope;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ControllerScopeFactoryTest {

  @Mock
  private PermissionMap permissionMap;

  private ControllerScopeFactory instance;

  private Context context = getContext();

  private List<String> DENY_ALL_LIST = List.of("denyAll");
  private List<String> ROLE_LIST = List.of("hasRole('role1')", "hasRole('role2')");

  @BeforeEach
  void setup() {
    instance = new ControllerScopeFactory(permissionMap);
  }

  @Test
  void shouldCreateControllerScope() {
    List<ControllerScope> resultList = instance.create(context);

    assertThat(resultList).hasSize(1);

    ControllerScope resultScope = resultList.get(0);
    assertThat(resultScope.getClassName()).isEqualTo(SCHEMA_NAME + "Controller");
    assertThat(resultScope.getSchemaName()).isEqualTo(SCHEMA_NAME);
    assertThat(resultScope.getPkName()).isEqualTo(PK_NAME);
    assertThat(resultScope.getEndpoint()).isEqualTo(ENDPOINT);
    assertThat(resultScope.getPkType()).isEqualTo(UUID.class.getCanonicalName());
  }

  @Test
  void shouldAddRolesToControllerScope() {
    given(permissionMap.getReadExpressionsFor(TABLE_NAME)).willReturn(DENY_ALL_LIST);
    given(permissionMap.getUpdateExpressionsFor(TABLE_NAME)).willReturn(DENY_ALL_LIST);
    given(permissionMap.getCreateExpressionsFor(TABLE_NAME)).willReturn(ROLE_LIST);
    given(permissionMap.getDeleteExpressionsFor(TABLE_NAME)).willReturn(ROLE_LIST);

    List<ControllerScope> resultList = instance.create(context);

    ControllerScope resultScope = resultList.get(0);
    assertThat(resultScope.getReadRoles()).containsAll(DENY_ALL_LIST);
    assertThat(resultScope.getUpdateRoles()).containsAll(DENY_ALL_LIST);
    assertThat(resultScope.getCreateRoles()).containsAll(ROLE_LIST);
    assertThat(resultScope.getDeleteRoles()).containsAll(ROLE_LIST);
  }
}