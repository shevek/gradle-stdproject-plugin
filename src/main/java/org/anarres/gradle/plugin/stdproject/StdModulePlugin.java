package org.anarres.gradle.plugin.stdproject;

import com.github.benmanes.gradle.versions.VersionsPlugin;
import com.google.common.collect.Sets;
import nebula.plugin.info.InfoPlugin;
import net.saliman.gradle.plugin.cobertura.CoberturaExtension;
import net.saliman.gradle.plugin.cobertura.CoberturaPlugin;
import org.gradle.api.JavaVersion;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.quality.FindBugsExtension;
import org.gradle.api.plugins.quality.FindBugsPlugin;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.api.tasks.testing.Test;
import org.gradle.api.tasks.testing.logging.TestExceptionFormat;
import org.gradle.api.tasks.testing.logging.TestLogEvent;
import org.gradle.api.tasks.testing.logging.TestLoggingContainer;
import org.gradle.external.javadoc.StandardJavadocDocletOptions;

/**
 * The standard module plugin.
 *
 * @author shevek
 */
public class StdModulePlugin implements Plugin<Project> {

    @Override
    public void apply(final Project project) {
        final StdModuleExtension extension = project.getExtensions().create("stdmodule", StdModuleExtension.class);

        // Convention
        project.getPlugins().apply(JavaPlugin.class);
        project.getPlugins().apply(InfoPlugin.class);
        project.getPlugins().apply(VersionsPlugin.class);

        project.getRepositories().add(project.getRepositories().mavenCentral());
        project.getRepositories().add(project.getRepositories().jcenter());

        Javadoc javadoc = (Javadoc) project.getTasks().getByName(JavaPlugin.JAVADOC_TASK_NAME);
        StandardJavadocDocletOptions javadocOptions = (StandardJavadocDocletOptions) javadoc.getOptions();
        javadocOptions.setLinks(extension.javadocLinks);    // afterEvaluate
        if (JavaVersion.current().isJava8Compatible())
            javadocOptions.addStringOption("Xdoclint:none", "-quiet");

        Test test = (Test) project.getTasks().getByName(JavaPlugin.TEST_TASK_NAME);
        test.systemProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
        test.systemProperty("org.apache.commons.logging.simplelog.defaultlog", "debug");
        test.systemProperty("org.apache.commons.logging.diagnostics.dest", "STDERR");
        test.systemProperty("org.slf4j.simpleLogger.logFile", "System.out");
        test.systemProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
        test.systemProperty("jna.nosys", "true");

        TestLoggingContainer testLogging = test.getTestLogging();
        testLogging.events(TestLogEvent.STARTED, TestLogEvent.PASSED, TestLogEvent.FAILED, TestLogEvent.SKIPPED);
        if (System.getProperty("test.single") != null) {
            testLogging.setShowStackTraces(true);
            testLogging.setShowExceptions(true);
            testLogging.setExceptionFormat(TestExceptionFormat.FULL);
            testLogging.setShowStandardStreams(true);
        }

        // Check
        project.getPlugins().apply(FindBugsPlugin.class);
        FindBugsExtension findbugs = project.getExtensions().getByType(FindBugsExtension.class);
        findbugs.setIgnoreFailures(true);   // Hope the plugin set this property as a convention.
        project.getTasks().getByName("findbugsTest").setEnabled(false);

        project.getPlugins().apply(CoberturaPlugin.class);
        CoberturaExtension cobertura = project.getExtensions().getByType(CoberturaExtension.class);
        cobertura.setCoverageFormats(Sets.newHashSet("html", "xml"));

    }

}
