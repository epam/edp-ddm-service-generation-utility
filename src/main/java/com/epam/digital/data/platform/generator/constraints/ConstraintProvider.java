package com.epam.digital.data.platform.generator.constraints;

import java.util.List;
import com.epam.digital.data.platform.generator.model.template.Constraint;

public interface ConstraintProvider {

  List<Constraint> getConstraintForProperty(String... propertyValues);
}
