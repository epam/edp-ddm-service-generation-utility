package ${basePackage}.bpwebservice.soap.dto;

import com.epam.digital.data.platform.bpwebservice.constant.Constants;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    namespace = Constants.NAMESPACE,
    name = ${className}.XML_TYPE_NAME
)
@Data
public class ${className} {

  public static final String XML_TYPE_NAME = "${className?uncapFirst}";

<#list fields as field>
  <#list field.constraints as constraint>
  ${constraint.name}(<#list constraint.content as content>${content.key} = ${content.value}<#sep>, </#list>)
  </#list>
  private ${field.type} ${field.name};
</#list>
}
