/*
 * Copyright 2022 EPAM Systems.
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

package com.epam.digital.data.platform.generator.scope;

import java.util.List;

import static java.util.Collections.emptyList;

public class ModifyControllerScope extends ControllerScope {
  private List<String> createRoles = emptyList();
  private List<String> updateRoles = emptyList();
  private List<String> deleteRoles = emptyList();
  private boolean bulkLoadEnabled;

  public List<String> getCreateRoles() {
    return createRoles;
  }

  public void setCreateRoles(List<String> createRoles) {
    this.createRoles = createRoles;
  }

  public List<String> getUpdateRoles() {
    return updateRoles;
  }

  public void setUpdateRoles(List<String> updateRoles) {
    this.updateRoles = updateRoles;
  }

  public List<String> getDeleteRoles() {
    return deleteRoles;
  }

  public void setDeleteRoles(List<String> deleteRoles) {
    this.deleteRoles = deleteRoles;
  }

  public boolean isBulkLoadEnabled() {
    return bulkLoadEnabled;
  }

  public void setBulkLoadEnabled(boolean bulkLoadEnabled) {
    this.bulkLoadEnabled = bulkLoadEnabled;
  }
}
