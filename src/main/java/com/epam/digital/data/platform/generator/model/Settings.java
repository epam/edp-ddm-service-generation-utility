package com.epam.digital.data.platform.generator.model;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

public class Settings {

  private General general;

  @JsonSetter(nulls = Nulls.SKIP)
  private Kafka kafka = new Kafka();

  public General getGeneral() {
    return general;
  }

  public void setGeneral(General general) {
    this.general = general;
  }

  public Kafka getKafka() {
    return kafka;
  }

  public void setKafka(Kafka kafka) {
    this.kafka = kafka;
  }
}
