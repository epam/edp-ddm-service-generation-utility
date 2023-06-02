/*
 * Copyright 2023 EPAM Systems.
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

package com.epam.digital.data.platform.generator.factory.impl;

import com.epam.digital.data.platform.generator.factory.ScopeFactory;
import com.epam.digital.data.platform.generator.metadata.RlsMetadata;
import com.epam.digital.data.platform.generator.metadata.SearchConditionProvider;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.model.template.GeoserverRlsEntry;
import com.epam.digital.data.platform.generator.scope.GeoserverRlsValuesScope;
import com.epam.digital.data.platform.generator.utils.DbUtils;
import org.springframework.stereotype.Component;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.Table;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class GeoserverRlsValuesScopeFactory implements ScopeFactory<GeoserverRlsValuesScope> {

  private static final String GEOMETRY_COLUMN_TYPE = "geometry";
  private final SearchConditionProvider searchConditionProvider;

  public GeoserverRlsValuesScopeFactory(SearchConditionProvider searchConditionProvider) {
    this.searchConditionProvider = searchConditionProvider;
  }

  @Override
  public List<GeoserverRlsValuesScope> create(Context context) {
    var scope = new GeoserverRlsValuesScope();
    var rlsData = searchConditionProvider.getRlsMetadata();
    var geoServerRlsEntries = mapRlsMetadataToGeoserverRlsEntries(rlsData, context);
    scope.setGeoRls(geoServerRlsEntries);
    return List.of(scope);
  }

  private List<GeoserverRlsEntry> mapRlsMetadataToGeoserverRlsEntries(
      List<RlsMetadata> rlsMetadataList, Context context) {
    return rlsMetadataList.stream()
        .flatMap(
            rlsMetadata -> {
              var table = DbUtils.findTable(rlsMetadata.getCheckTable(), context);
              if (!isTableContainingGeometryColumn(table)) {
                return Stream.empty();
              }
              var entry = new GeoserverRlsEntry();
              entry.setRls(rlsMetadata);
              entry.setGeometryColumn(getColumnWithGeometry(table));
              return Stream.of(entry);
            })
        .collect(Collectors.toList());
  }

  private boolean isTableContainingGeometryColumn(Table table) {
    return table.getColumns().stream()
        .map(column -> column.getColumnDataType().getName())
        .anyMatch(GEOMETRY_COLUMN_TYPE::equalsIgnoreCase);
  }

  private String getColumnWithGeometry(Table table) {
    return table.getColumns().stream()
        .filter(column -> GEOMETRY_COLUMN_TYPE.equalsIgnoreCase(column.getColumnDataType().getName()))
        .map(NamedObject::getName)
        .findFirst()
        .orElse(null);
  }

  @Override
  public String getPath() {
    return "geoserver-rls/deploy-templates/values.yaml.ftl";
  }
}
