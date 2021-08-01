package com.epam.digital.data.platform.generator.metadata;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("ddm_liquibase_metadata")
class Metadata {

  @Id
  @Column("metadata_id")
  private Long id;

  private String changeType;
  private String changeName;

  @Column("attribute_name")
  private String name;
  @Column("attribute_value")
  private String value;

  public Metadata(Long id, String changeType, String changeName, String name, String value) {
    this.id = id;
    this.changeType = changeType;
    this.changeName = changeName;
    this.name = name;
    this.value = value;
  }

  public Long getId() {
    return id;
  }

  public String getChangeType() {
    return changeType;
  }

  public String getChangeName() {
    return changeName;
  }

  public String getName() {
    return name;
  }

  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return "Metadata{" +
        "id=" + id +
        ", changeType='" + changeType + '\'' +
        ", changeName='" + changeName + '\'' +
        ", name='" + name + '\'' +
        ", value='" + value + '\'' +
        '}';
  }
}
