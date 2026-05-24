package com.tractorstore.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

class ArchUnitInternalAccessTest {

  @Test
  void shouldNotAccessOtherModulesInternalPackages() {
    var imported = new ClassFileImporter()
        .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
        .importPackages("com.tractorstore");

    noClasses()
        .that().resideOutsideOfPackage("..shared..")
        .should().accessClassesThat().resideInAnyPackage("..internal..")
        .check(imported);
  }
}
