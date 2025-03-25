package com.springapplication.userapp;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

public class ArchitectureTest {

    private static final String PROJECT_ROOT = "com.springapplication.userapp";

    private final JavaClasses classes = new ClassFileImporter().importPackages(PROJECT_ROOT);
    private final JavaClasses mainClasses = new ClassFileImporter()
            .withImportOption(new ImportOption.DoNotIncludeTests())
            .importPackages(PROJECT_ROOT);

    @Test
    void providersHaveNoDependenciesRule() {

        var rule = noClasses()
                .that()
                .resideInAPackage(PROJECT_ROOT + ".providers..")
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(PROJECT_ROOT + ".core..", PROJECT_ROOT + ".configuration..");

        rule.check(classes);
    }

    @Test
    void domainDoesNotDependOnAdaptersRule() {
        Set.of(PROJECT_ROOT + ".core").forEach(scope -> {
            var rule = noClasses()
                    .that()
                    .resideInAPackage(scope + ".domain..")
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage(scope + ".adapters");

            rule.check(classes);
        });
    }

    @Test
    void domainApplicationsAreNotAccessibleDirectly() {

        classes()
                .that()
                .resideInAnyPackage(PROJECT_ROOT + ".core.domain.application..")
                .should()
                .bePackagePrivate()
                .orShould()
                .bePrivate()
                .check(mainClasses);
    }
}
