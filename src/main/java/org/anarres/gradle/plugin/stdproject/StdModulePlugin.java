package org.anarres.gradle.plugin.stdproject;

import be.insaneprogramming.gradle.animalsniffer.AnimalSnifferExtension;
import be.insaneprogramming.gradle.animalsniffer.AnimalSnifferPlugin;
import com.bmuschko.gradle.nexus.NexusPlugin;
import com.github.benmanes.gradle.versions.VersionsPlugin;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import groovy.lang.Closure;
import java.util.HashMap;
import java.util.Map;
import nebula.plugin.info.InfoPlugin;
import net.saliman.gradle.plugin.cobertura.CoberturaExtension;
import net.saliman.gradle.plugin.cobertura.CoberturaPlugin;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.gradle.api.Action;
import org.gradle.api.JavaVersion;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.maven.MavenDeployer;
import org.gradle.api.artifacts.maven.MavenPom;
import org.gradle.api.artifacts.maven.MavenResolver;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.plugins.quality.FindBugsExtension;
import org.gradle.api.plugins.quality.FindBugsPlugin;
import org.gradle.api.tasks.Upload;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.api.tasks.testing.Test;
import org.gradle.api.tasks.testing.logging.TestExceptionFormat;
import org.gradle.api.tasks.testing.logging.TestLogEvent;
import org.gradle.api.tasks.testing.logging.TestLoggingContainer;
import org.gradle.util.ConfigureUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The standard module plugin.
 *
 * @author shevek
 */
public class StdModulePlugin implements Plugin<Project> {

    private static final Logger LOG = LoggerFactory.getLogger(StdModulePlugin.class);

    @Override
    public void apply(final Project project) {
        // Root
        project.getRootProject().getPlugins().apply(StdProjectPlugin.class);

        // Extensions
        // final StdProjectExtension rootExtension = project.getRootProject().getExtensions().getByType(StdProjectExtension.class);
        final StdModuleExtension extension = project.getExtensions().create("stdmodule", StdModuleExtension.class, project);

        // Convention
        project.getPlugins().apply(JavaPlugin.class);
        project.getPlugins().apply(InfoPlugin.class);
        project.getPlugins().apply(VersionsPlugin.class);

        JavaPluginConvention java = project.getConvention().getPlugin(JavaPluginConvention.class);
        java.setSourceCompatibility(JavaVersion.VERSION_1_7);

        project.getRepositories().add(project.getRepositories().mavenCentral());
        project.getRepositories().add(project.getRepositories().jcenter());

        project.getDependencies().add("testCompile", "junit:junit:4.12");
        project.getDependencies().add("testCompile", "org.slf4j:slf4j-api:1.7.12");
        project.getDependencies().add("testRuntime", "ch.qos.logback:logback-classic:1.1.3");

        // Javadoc javadoc = (Javadoc) project.getTasks().getByName(JavaPlugin.JAVADOC_TASK_NAME);
        for (Javadoc javadoc : project.getTasks().withType(Javadoc.class))
            StdTaskConfiguration.configureJavadoc(project, javadoc);

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
        // https://github.com/stevesaliman/gradle-cobertura-plugin/issues/81
        // cobertura.setCoberturaVersion("2.1.1");

        project.getPlugins().apply(AnimalSnifferPlugin.class);
        AnimalSnifferExtension animalSniffer = project.getExtensions().getByType(AnimalSnifferExtension.class);
        animalSniffer.setSignature("org.codehaus.mojo.signature:java17:+@signature");

        // Nexus
        project.getPlugins().apply(NexusPlugin.class);
        // final NexusPluginExtension nexus = project.getExtensions().getByType(NexusPluginExtension.class);
        project.afterEvaluate(new Action<Project>() {
            @Override
            public void execute(Project t) {

                for (Upload upload : project.getTasks().withType(Upload.class)) {
                    for (MavenDeployer deployer : upload.getRepositories().withType(MavenDeployer.class))
                        deployer.setUniqueVersion(false);

                    for (MavenResolver resolver : upload.getRepositories().withType(MavenResolver.class)) {
                        MavenPom pom = resolver.getPom();
                        // We can't cast (Model) pom.getModel() because of FilteringClassLoader.

                        /*
                         t.getLogger().info("Task " + upload + " pom " + pom + " of " + pom.getClass());
                         t.getLogger().info("Task " + upload + " model " + pom.getModel() + " of " + pom.getModel().getClass());
                         CompositeQuery q = new CompositeQuery();
                         q.add(new ClassQuery(pom.getClass()));
                         q.add(new ClassQuery(pom.getModel().getClass()));
                         q.add(new ClassQuery(getClass()));
                         q.add(new ClassLoaderQuery("maven", pom.getModel().getClass().getClassLoader()));
                         q.add(new ClassLoaderQuery("self", getClass().getClassLoader()));
                         t.getLogger().info(String.valueOf(q.call()));
                         */
                        // developers is a List<Developer> and licenses is a List<License>
                        // so they need special treatment.
                        pom.project(new Closure(StdModulePlugin.this) {
                            @Override
                            public Object call(Object... args) {
                                final Object pom = getDelegate();  // It's a CustomModelBuilder extends ModelBuilder extends FactoryBuilderSupport

                                DefaultGroovyMethods.invokeMethod(pom, "licenses", new Closure(StdModulePlugin.this) {
                                    // This delegates to the same ModelBuilder, with an internal state
                                    // change to represent that we're now in the developers{} block.
                                    // LOG.debug("Developers delegate is {}", getDelegate());
                                    @Override
                                    public Object call(Object... args) {
                                        for (License license : extension.licenses) {
                                            Object target = DefaultGroovyMethods.invokeMethod(pom, "license", null);
                                            ConfigureUtil.configureByMap(ImmutableMap.<String, Object>of(
                                                    "name", Preconditions.checkNotNull(license.getName(), "License name was null."),
                                                    "url", Preconditions.checkNotNull(license.getUri(), "License URI was null."),
                                                    "distribution", "repo"
                                            ), target);
                                        }
                                        return null;
                                    }
                                });

                                DefaultGroovyMethods.invokeMethod(pom, "developers", new Closure(StdModulePlugin.this) {
                                    @Override
                                    public Object call(Object... args) {
                                        for (StdModuleExtension.Person person : extension.authors) {
                                            Object target = DefaultGroovyMethods.invokeMethod(pom, "developer", null);
                                            ConfigureUtil.configureByMap(ImmutableMap.<String, Object>of(
                                                    "id", Preconditions.checkNotNull(person.id, "Person.id was null."),
                                                    "name", Preconditions.checkNotNull(person.name, "Person.name was null."),
                                                    "email", Preconditions.checkNotNull(person.email, "Person.email was null.")
                                            ), target);
                                            if (LOG.isDebugEnabled())
                                                LOG.debug("Developer value is {} with props {}", target, DefaultGroovyMethods.getProperties(target));
                                        }
                                        return null;
                                    }
                                });

                                return null;
                            }
                        });

                        // Everything else is simple, and we can do this:
                        Map<String, Object> pomData = new HashMap<String, Object>();
                        pomData.put("name", project.getName());
                        pomData.put("description", extension.description);
                        pomData.put("url", extension.url);
                        pomData.put("inceptionYear", extension.inceptionYear);
                        pomData.put("scm", ImmutableMap.<String, Object>of(
                                "connection", extension.vcsUrl,
                                "url", extension.vcsUrl,
                                "developerConnection", extension.vcsUrl
                        ));
                        pomData.put("issueManagement", ImmutableMap.<String, Object>of(
                                "system", extension.issuesSystem,
                                "url", extension.issuesUrl
                        ));
                        t.getLogger().info("Configuring " + pom + " with " + pomData);
                        ConfigureUtil.configureByMap(pomData, pom.getModel());
                    }
                }

            }
        });
    }

}
