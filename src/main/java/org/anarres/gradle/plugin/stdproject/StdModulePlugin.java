package org.anarres.gradle.plugin.stdproject;

import be.insaneprogramming.gradle.animalsniffer.AnimalSnifferExtension;
import be.insaneprogramming.gradle.animalsniffer.AnimalSnifferPlugin;
import com.github.benmanes.gradle.versions.VersionsPlugin;
import com.google.common.collect.Sets;
import java.util.Map;
import javax.annotation.Nonnull;
import nebula.plugin.info.InfoPlugin;
import net.saliman.gradle.plugin.cobertura.CoberturaExtension;
import net.saliman.gradle.plugin.cobertura.CoberturaPlugin;
import org.gradle.api.Action;
import org.gradle.api.JavaVersion;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
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

    public static void configureJavadoc(@Nonnull final Project project, @Nonnull final Javadoc javadoc) {
        final StdProjectExtension extension = (StdProjectExtension) project.getRootProject().getExtensions().getByName("stdproject");
        final StandardJavadocDocletOptions javadocOptions = (StandardJavadocDocletOptions) javadoc.getOptions();
        if (JavaVersion.current().isJava8Compatible())
            javadocOptions.addStringOption("Xdoclint:none", "-quiet");
        javadoc.doFirst(new Action<Task>() {
            @Override
            public void execute(Task t) {
                javadocOptions.setLinkSource(true);
                javadocOptions.setLinks(extension.javadocLinks);
            }
        });
    }

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
        configureJavadoc(project, javadoc);

        Test test = (Test) project.getTasks().getByName(JavaPlugin.TEST_TASK_NAME);
        test.systemProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
        test.systemProperty("org.apache.commons.logging.simplelog.defaultlog", "debug");
        test.systemProperty("org.apache.commons.logging.diagnostics.dest", "STDERR");
        test.systemProperty("org.slf4j.simpleLogger.logFile", "System.out");
        test.systemProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
        test.systemProperty("jna.nosys", "true");
        final String testSystemPropertyPrefix = "test-sys-prop.";
        for (Map.Entry<Object, ? extends Object> e : System.getProperties().entrySet()) {
            String key = String.valueOf(e.getKey());
            if (key.startsWith(testSystemPropertyPrefix))
                test.systemProperty(key.substring(testSystemPropertyPrefix.length()), e.getValue());
        }
        for (Map.Entry<String, ? extends Object> e : project.getExtensions().getExtraProperties().getProperties().entrySet()) {
            String key = e.getKey();
            if (key.startsWith(testSystemPropertyPrefix))
                test.systemProperty(key.substring(testSystemPropertyPrefix.length()), e.getValue());
        }

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

        project.getPlugins().apply(AnimalSnifferPlugin.class);
        AnimalSnifferExtension animalSniffer = project.getExtensions().getByType(AnimalSnifferExtension.class);
        animalSniffer.setSignature("org.codehaus.mojo.signature:java17:+@signature");
    }

}
