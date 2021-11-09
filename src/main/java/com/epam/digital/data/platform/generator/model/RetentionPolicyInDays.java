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
