package com.epam.digital.data.platform.generator.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Kafka {

  @JsonProperty("retention-policy-in-days")
  private RetentionPolicyInDays retentionPolicyInDays;

  public RetentionPolicyInDays getRetentionPolicyInDays() {
    return retentionPolicyInDays;
  }

  public void setRetentionPolicyInDays(RetentionPolicyInDays retentionPolicyInDays) {
    this.retentionPolicyInDays = retentionPolicyInDays;
  }
}
