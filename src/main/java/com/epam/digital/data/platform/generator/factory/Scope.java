package com.epam.digital.data.platform.generator.factory;

import com.epam.digital.data.platform.generator.model.Context;
import java.util.List;

public interface Scope<T> {

  List<T> create(Context context);
}
