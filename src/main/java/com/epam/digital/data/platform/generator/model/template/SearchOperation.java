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

package com.epam.digital.data.platform.generator.model.template;

import java.util.List;

public class SearchOperation {

  private SearchOperatorType operator;
  private String operationName;
  private List<SearchConditionField> equalFields;
  private List<SearchConditionField> notEqualFields;
  private List<SearchConditionField> startsWithFields;
  private List<SearchConditionField> startsWithArrayFields;
  private List<SearchConditionField> containsFields;
  private List<SearchConditionField> inFields;
  private List<SearchConditionField> notInFields;
  private List<SearchConditionField> betweenFields;
  private List<SearchOperation> nestedSearchOperations;

  public SearchOperatorType getOperator() {
    return operator;
  }

  public void setOperator(SearchOperatorType operator) {
    this.operator = operator;
  }

  public String getOperationName() {
    return operationName;
  }

  public void setOperationName(String operationName) {
    this.operationName = operationName;
  }

  public List<SearchConditionField> getEqualFields() {
    return equalFields;
  }

  public void setEqualFields(List<SearchConditionField> equalFields) {
    this.equalFields = equalFields;
  }

  public List<SearchConditionField> getNotEqualFields() {
    return notEqualFields;
  }

  public void setNotEqualFields(List<SearchConditionField> notEqualFields) {
    this.notEqualFields = notEqualFields;
  }

  public List<SearchConditionField> getStartsWithFields() {
    return startsWithFields;
  }

  public void setStartsWithFields(List<SearchConditionField> startsWithFields) {
    this.startsWithFields = startsWithFields;
  }

  public List<SearchConditionField> getStartsWithArrayFields() {
    return startsWithArrayFields;
  }

  public void setStartsWithArrayFields(List<SearchConditionField> startsWithArrayFields) {
    this.startsWithArrayFields = startsWithArrayFields;
  }

  public List<SearchConditionField> getContainsFields() {
    return containsFields;
  }

  public void setContainsFields(List<SearchConditionField> containsFields) {
    this.containsFields = containsFields;
  }

  public List<SearchConditionField> getInFields() {
    return inFields;
  }

  public void setInFields(List<SearchConditionField> inFields) {
    this.inFields = inFields;
  }

  public List<SearchConditionField> getNotInFields() {
    return notInFields;
  }

  public void setNotInFields(List<SearchConditionField> notInFields) {
    this.notInFields = notInFields;
  }

  public List<SearchConditionField> getBetweenFields() {
    return betweenFields;
  }

  public void setBetweenFields(List<SearchConditionField> betweenFields) {
    this.betweenFields = betweenFields;
  }

  public List<SearchOperation> getNestedSearchOperations() {
    return nestedSearchOperations;
  }

  public void setNestedSearchOperations(List<SearchOperation> nestedSearchOperations) {
    this.nestedSearchOperations = nestedSearchOperations;
  }
}
