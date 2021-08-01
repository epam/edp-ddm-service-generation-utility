package com.epam.digital.data.platform.generator.scope;

import java.util.List;
import java.util.Map;

public class RestApplicationYamlScope extends ApplicationYamlScope {

  private Map<String, List<String>> entityPaths;
  private List<String> searchPaths;
  private boolean isEnumPresent;
  private Integer retentionPolicyDaysRead;
  private Integer retentionPolicyDaysWrite;

  public Map<String, List<String>> getEntityPaths() {
    return entityPaths;
  }

  public void setEntityPaths(Map<String, List<String>> entityPaths) {
    this.entityPaths = entityPaths;
  }

  public List<String> getSearchPaths() {
    return searchPaths;
  }

  public void setSearchPaths(List<String> searchPaths) {
    this.searchPaths = searchPaths;
  }

  public boolean isEnumPresent() {
    return isEnumPresent;
  }

  public void setEnumPresent(boolean enumPresent) {
    isEnumPresent = enumPresent;
  }

  public Integer getRetentionPolicyDaysRead() {
    return retentionPolicyDaysRead;
  }

  public void setRetentionPolicyDaysRead(Integer retentionPolicyDaysRead) {
    this.retentionPolicyDaysRead = retentionPolicyDaysRead;
  }

  public Integer getRetentionPolicyDaysWrite() {
    return retentionPolicyDaysWrite;
  }

  public void setRetentionPolicyDaysWrite(Integer retentionPolicyDaysWrite) {
    this.retentionPolicyDaysWrite = retentionPolicyDaysWrite;
  }
}
