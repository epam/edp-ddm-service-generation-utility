package ${basePackage}.bpwebservice.soap.factory;

<#list classNames as className>
import ${basePackage}.bpwebservice.soap.dto.${className};
</#list>
import com.epam.digital.data.platform.bpwebservice.constant.Constants;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import org.springframework.stereotype.Component;

@Component
@XmlRegistry
public class XmlObjectFactory {

<#list classNames as className>
  @XmlElementDecl(namespace = Constants.NAMESPACE, name = ${className}.XML_TYPE_NAME)
  public JAXBElement<${className}> create${className}(${className} value) {
    var qName = new QName(Constants.NAMESPACE, ${className}.XML_TYPE_NAME);
    return new JAXBElement<>(qName, ${className}.class, null, value);
  }

 </#list>
}