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

package com.epam.digital.data.platform.generator.model.template;

import java.util.List;
import java.util.Objects;

public class Field {

  private String type;
  private String name;
  private List<Constraint> constraints;

  public Field() {
  }

  public Field(String type, String name,
      List<Constraint> constraints) {
    this.type = type;
    this.name = name;
    this.constraints = constraints;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Constraint> getConstraints() {
    return constraints;
  }

  public void setConstraints(
      List<Constraint> constraints) {
    this.constraints = constraints;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Field field = (Field) o;
    return Objects.equals(type, field.type) && Objects.equals(name, field.name)
        && Objects.equals(constraints, field.constraints);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, name, constraints);
  }
}
