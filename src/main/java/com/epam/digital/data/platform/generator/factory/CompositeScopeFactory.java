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

package com.epam.digital.data.platform.generator.factory;

import static java.util.stream.Collectors.toList;

import com.epam.digital.data.platform.generator.model.Context;
import java.util.List;

public class CompositeScopeFactory implements ScopeFactory {

  private final List<ScopeFactory<?>> scopeFactories;

  public CompositeScopeFactory(List<ScopeFactory<?>> scopeFactories) {
    this.scopeFactories = scopeFactories;
  }

  @Override
  public String getPath() {
    return scopeFactories.get(0).getPath();
  }

  @Override
  public List create(Context context) {
    return scopeFactories.stream()
        .map(x -> x.create(context))
        .flatMap(List::stream)
        .collect(toList());
  }
}
