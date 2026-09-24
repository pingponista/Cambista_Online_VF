package com.cambistaonline.auth.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

public class OnionArchitectureTest {

    private JavaClasses importedClasses;

    @BeforeEach
    void setUp() {
        importedClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("com.cambistaonline.auth");
    }

    @Test
    void domainLayerShouldNotDependOnAnyOuterLayersOrFrameworks() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..auth.domain..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("..auth.application..", "..auth.infrastructure..", "org.springframework..");

        rule.check(importedClasses);
    }

    @Test
    void applicationLayerShouldOnlyDependOnDomain() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..auth.application..")
                .should().dependOnClassesThat()
                .resideInAPackage("..auth.infrastructure..");

        rule.check(importedClasses);
    }

    @Test
    void infrastructureLayerShouldNotBeDependedOnByDomainOrApplication() {
        ArchRule rule = classes()
                .that().resideInAPackage("..auth.infrastructure..")
                .should().onlyHaveDependentClassesThat()
                .resideInAnyPackage("..auth.infrastructure..", "..auth.config..", "..auth.architecture..", "com.cambistaonline..");

        rule.check(importedClasses);
    }
}
