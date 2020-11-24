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

import schemacrawler.schema.Catalog;

public final class Context {

  private final Settings settings;
  private final Catalog catalog;
  private final AsyncData asyncData;

  public Context(
      Settings settings, Catalog catalog, AsyncData asyncData) {
    this.settings = settings;
    this.catalog = catalog;
    this.asyncData = asyncData;
  }

  public Settings getSettings() {
    return settings;
  }

  public Catalog getCatalog() {
    return catalog;
  }

  public AsyncData getAsyncData() { return asyncData; }

}
