package com.epam.digital.data.platform.generator.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

public class Kafka {

  @JsonSetter(nulls = Nulls.SKIP)
  @JsonProperty("retention-policy-in-days")
  private RetentionPolicyInDays retentionPolicyInDays = new RetentionPolicyInDays();

  public RetentionPolicyInDays getRetentionPolicyInDays() {
    return retentionPolicyInDays;
  }

  public void setRetentionPolicyInDays(RetentionPolicyInDays retentionPolicyInDays) {
    this.retentionPolicyInDays = retentionPolicyInDays;
  }
}
