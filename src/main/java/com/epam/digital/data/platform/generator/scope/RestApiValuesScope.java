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

package com.epam.digital.data.platform.generator.scope;

import java.util.Set;

public class RestApiValuesScope {

  private int replicationFactor;
  private String s3Signer;
  private ExposedSearchConditionsInfo exposedToPlatformInfo;
  private ExposedSearchConditionsInfo exposedToExternalInfo;
  private ExposedSearchConditionsInfo exposedToPublicInfo;
  private String stageName;

  public int getReplicationFactor() {
    return replicationFactor;
  }

  public void setReplicationFactor(int replicationFactor) {
    this.replicationFactor = replicationFactor;
  }

  public String getS3Signer() {
    return s3Signer;
  }

  public void setS3Signer(String s3Signer) {
    this.s3Signer = s3Signer;
  }

  public ExposedSearchConditionsInfo getExposedToPlatformInfo() {
    return exposedToPlatformInfo;
  }

  public void setExposedToPlatformInfo(ExposedSearchConditionsInfo exposedToPlatformInfo) {
    this.exposedToPlatformInfo = exposedToPlatformInfo;
  }

  public ExposedSearchConditionsInfo getExposedToExternalInfo() {
    return exposedToExternalInfo;
  }

  public void setExposedToExternalInfo(ExposedSearchConditionsInfo exposedToExternalInfo) {
    this.exposedToExternalInfo = exposedToExternalInfo;
  }

  public ExposedSearchConditionsInfo getExposedToPublicInfo() {
    return exposedToPublicInfo;
  }

  public void setExposedToPublicInfo(ExposedSearchConditionsInfo exposedToPublicInfo) {
    this.exposedToPublicInfo = exposedToPublicInfo;
  }

  public String getStageName() {
    return stageName;
  }

  public void setStageName(String stageName) {
    this.stageName = stageName;
  }

  public static class ExposedSearchConditionsInfo {
    private Set<String> paths;
    private boolean anyResponseContainsFile;

    public Set<String> getPaths() {
      return paths;
    }

    public void setPaths(Set<String> paths) {
      this.paths = paths;
    }

    public boolean isAnyResponseContainsFile() {
      return anyResponseContainsFile;
    }

    public void setAnyResponseContainsFile(boolean anyResponseContainsFile) {
      this.anyResponseContainsFile = anyResponseContainsFile;
    }
  }
}