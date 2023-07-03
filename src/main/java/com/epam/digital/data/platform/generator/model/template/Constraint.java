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

public class Constraint {
  private String name;
  private List<Content> content;

  public Constraint() {}

  public Constraint(String name, List<Content> content) {
    this.name = name;
    this.content = content;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Content> getContent() {
    return content;
  }

  public void setContent(
      List<Content> content) {
    this.content = content;
  }

  public static class Content {
    private String key;
    private String value;

    public Content() {}

    public Content(String key, String value) {
      this.key = key;
      this.value = value;
    }

    public String getKey() {
      return key;
    }

    public void setKey(String key) {
      this.key = key;
    }

    public Object getValue() {
      return value;
    }

    public void setValue(String value) {
      this.value = value;
    }
  }
}
