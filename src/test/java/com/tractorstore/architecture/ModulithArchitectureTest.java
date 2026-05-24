package com.tractorstore.architecture;

import com.tractorstore.TractorStoreApplication;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ModulithArchitectureTest {

  @Test
  void shouldVerifyApplicationModules() {
    ApplicationModules.of(TractorStoreApplication.class).verify();
  }
}
