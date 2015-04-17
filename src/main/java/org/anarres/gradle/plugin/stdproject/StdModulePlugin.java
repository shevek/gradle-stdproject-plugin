package org.anarres.gradle.plugin.stdproject;

import be.insaneprogramming.gradle.animalsniffer.AnimalSnifferExtension;
import be.insaneprogramming.gradle.animalsniffer.AnimalSnifferPlugin;
import com.bmuschko.gradle.nexus.NexusPlugin;
import com.bmuschko.gradle.nexus.NexusPluginExtension;
import com.github.benmanes.gradle.versions.VersionsPlugin;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.HashMap;
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
import org.gradle.external.javadoc.StandardJavadocDocletOptions;
import org.gradle.util.ConfigureUtil;

/**
 * The standard module plugin.
 *
 * @author shevek
 */
public class StdModulePlugin implements Plugin<Project> {

    public static void configureJavadoc(@Nonnull final Project project, @Nonnull final Javadoc javadoc) {
        final StdProjectExtension extension = project.getRootProject().getExtensions().getByType(StdProjectExtension.class);
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

        // Nexus
        project.getPlugins().apply(NexusPlugin.class);
        final NexusPluginExtension nexus = project.getExtensions().getByType(NexusPluginExtension.class);
        project.afterEvaluate(new Action<Project>() {
            @Override
            public void execute(Project t) {

                for (Upload upload : project.getTasks().withType(Upload.class)) {
                    for (MavenDeployer deployer : upload.getRepositories().withType(MavenDeployer.class))
                        deployer.setUniqueVersion(false);

                    for (MavenResolver resolver : upload.getRepositories().withType(MavenResolver.class)) {
                        MavenPom pom = resolver.getPom();
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

                        Map<String, Object> pomData = new HashMap<String, Object>();
                        pomData.put("name", project.getName());
                        pomData.put("description", extension.projectDescription);
                        if (false)
                        pomData.put("developers", Lists.transform(extension.projectAuthors, new Function<StdModuleExtension.Person, Map<String, Object>>() {
                            @Override
                            public Map<String, Object> apply(StdModuleExtension.Person input) {
                                return ImmutableMap.<String, Object>of(
                                        "id", input.id,
                                        "name", input.name,
                                        "email", input.email
                                );
                            }
                        }));
                        pomData.put("url", extension.projectUrl);
                        pomData.put("inceptionYear", extension.projectInceptionYear);
                        pomData.put("scm", ImmutableMap.<String, Object>of(
                                "connection", extension.projectVcsUrl,
                                "url", extension.projectVcsUrl,
                                "developerConnection", extension.projectVcsUrl
                        ));
                        pomData.put("issueManagement", ImmutableMap.<String, Object>of(
                                "system", "github",
                                "url", extension.projectIssuesUrl
                        ));
                        t.getLogger().info("Configuring " + pom + " with " + pomData);
                        ConfigureUtil.configureByMap(pomData, pom.getModel());

                        /*
                         Model model = (Model) pom.getModel();
                         model.setName(project.getName());
                         model.setDescription(extension.projectDescription);
                         for (StdModuleExtension.Author author : extension.projectAuthors) {
                         Developer developer = new Developer();
                         developer.setId(author.id);
                         developer.setName(author.name);
                         developer.setEmail(author.email);
                         model.addDeveloper(developer);
                         }

                         // for (String license : extension.projectLicenses) { }
                         model.setUrl(extension.projectUrl);
                         model.setInceptionYear(extension.projectInceptionYear);

                         Scm scm = new Scm();
                         scm.setConnection(extension.projectVcsUrl);
                         scm.setUrl(extension.projectVcsUrl);
                         scm.setDeveloperConnection(extension.projectVcsUrl);
                         model.setScm(scm);

                         IssueManagement issueManagement = new IssueManagement();
                         issueManagement.setSystem("github");
                         issueManagement.setUrl(extension.projectIssuesUrl);
                         model.setIssueManagement(issueManagement);
                         */
                    }
                }

            }
        }
        );
    }

}
