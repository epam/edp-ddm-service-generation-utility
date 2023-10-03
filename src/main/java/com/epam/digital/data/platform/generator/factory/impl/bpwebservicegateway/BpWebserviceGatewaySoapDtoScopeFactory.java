/*
 * Copyright 2023 EPAM Systems.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.epam.digital.data.platform.generator.factory.impl.bpwebservicegateway;

import com.epam.digital.data.platform.generator.factory.ScopeFactory;
import com.epam.digital.data.platform.generator.model.Context;
import com.epam.digital.data.platform.generator.model.TrembitaProcessDefinitions;
import com.epam.digital.data.platform.generator.model.template.Constraint;
import com.epam.digital.data.platform.generator.model.template.Constraint.Content;
import com.epam.digital.data.platform.generator.model.template.Field;
import com.epam.digital.data.platform.generator.scope.BpWebserviceGatewayDtoScope;
import com.epam.digital.data.platform.generator.utils.ProcessDefinitionIdConverter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BpWebserviceGatewaySoapDtoScopeFactory extends
    AbstractBpWebserviceGatewayScopeFactory implements ScopeFactory<BpWebserviceGatewayDtoScope> {

  protected static final String XML_ELEMENT_ANNOTATION = "@XmlElement";
  protected static final String STRING_TYPE = "String";
  protected static final String MAP_STRING_STRING_TYPE = "Map<String, String>";
  protected static final String MAP_STRING_OBJECT_TYPE = "Map<String, Object>";
  protected static final String ATTRIBUTE_REQUIRED = "required";
  protected static final String BUSINESS_PROCESS_DEFINITION_KEY_FIELD = "businessProcessDefinitionKey";
  protected static final String START_VARIABLES_FIELD = "startVariables";
  protected static final String RESULT_VARIABLES_FIELD = "resultVariables";

  public BpWebserviceGatewaySoapDtoScopeFactory(
      TrembitaProcessDefinitions processDefinitions) {
    super(processDefinitions);
  }

  @Override
  public List<BpWebserviceGatewayDtoScope> create(Context context) {
    var scopes = new ArrayList<BpWebserviceGatewayDtoScope>();
    scopes.addAll(generateClassScopes(SUFFIX_REQUEST));
    scopes.addAll(generateClassScopes(SUFFIX_RESPONSE));
    return scopes;
  }

  private List<BpWebserviceGatewayDtoScope> generateClassScopes(String classNameSuffix) {
    var classScopes = processDefinitions.getProcessDefinitions().stream()
        .map(pd -> {
          var scope = new BpWebserviceGatewayDtoScope();
          var convertedId = ProcessDefinitionIdConverter.convert(pd.getProcessDefinitionId());
          scope.setClassName(generateClassName(convertedId, classNameSuffix));
          scope.setFields(prepareFields(classNameSuffix));
          return scope;
        }).collect(Collectors.toList());
    var defaultClassScope = new BpWebserviceGatewayDtoScope();
    defaultClassScope.setClassName(generateClassName(DEFAULT_ENDPOINT, classNameSuffix));
    defaultClassScope.setFields(prepareDefaultFields(classNameSuffix));
    classScopes.add(defaultClassScope);
    return classScopes;
  }

  private List<Field> prepareDefaultFields(String classNameSuffix) {
    var defaultFields = new ArrayList<Field>();
    if (SUFFIX_REQUEST.equals(classNameSuffix)) {
      defaultFields.add(
          generateFieldByParams(STRING_TYPE, BUSINESS_PROCESS_DEFINITION_KEY_FIELD, Boolean.TRUE));
    }
    defaultFields.addAll(prepareFields(classNameSuffix));
    return defaultFields;
  }

  private List<Field> prepareFields(String classNameSuffix) {
    var fields = new ArrayList<Field>();
    if (SUFFIX_REQUEST.equals(classNameSuffix)) {
      fields.add(
          generateFieldByParams(MAP_STRING_STRING_TYPE, START_VARIABLES_FIELD, Boolean.FALSE));
    } else {
      fields.add(
          generateFieldByParams(MAP_STRING_OBJECT_TYPE, RESULT_VARIABLES_FIELD, Boolean.TRUE));
    }
    return fields;
  }

  private Field generateFieldByParams(String type, String name, Boolean isRequiredAttr) {
    var field = new Field();
    field.setType(type);
    field.setName(name);
    var fieldConstraint = new Constraint();
    fieldConstraint.setName(XML_ELEMENT_ANNOTATION);
    var fieldConstraintContent = new Content();
    fieldConstraintContent.setKey(ATTRIBUTE_REQUIRED);
    fieldConstraintContent.setValue(isRequiredAttr.toString());
    fieldConstraint.setContent(List.of(fieldConstraintContent));
    field.setConstraints(List.of(fieldConstraint));
    return field;
  }

  @Override
  public String getPath() {
    return "bp-webservice-gateway/src/main/java/bpwebservice/soap/dto/dto.java.ftl";
  }
}
