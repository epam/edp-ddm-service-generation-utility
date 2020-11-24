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

package com.epam.digital.data.platform.generator.model;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

public class RetentionPolicyInDays {

  @JsonSetter(nulls = Nulls.SKIP)
  private Integer read = 3 * 365;

  @JsonSetter(nulls = Nulls.SKIP)
  private Integer write = 3 * 365;

  public Integer getRead() {
    return read;
  }

  public void setRead(Integer read) {
    this.read = read;
  }

  public Integer getWrite() {
    return write;
  }

  public void setWrite(Integer write) {
    this.write = write;
  }
}
