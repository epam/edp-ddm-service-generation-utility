package ${basePackage}.model.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class ${className} {
<#list fields as field>
  <#list field.constraints as constraint>
  ${constraint.name}(<#list constraint.content as content>${content.key} = ${content.value}<#sep>, </#list>)
  </#list>
  private ${field.type} ${field.name};
</#list>

<#list fields as field>
  public ${field.type} get${field.name?cap_first}() {
    return this.${field.name};
  }

  public void set${field.name?cap_first}(${field.type} ${field.name}) {
    this.${field.name}=${field.name};
  }
</#list>
}
