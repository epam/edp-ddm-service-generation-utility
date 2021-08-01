package com.epam.digital.data.platform.generator.model;

public class Settings {

  private General general;
  private Kafka kafka;

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
