package org.anarres.gradle.plugin.stdproject;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.Callable;
import javax.annotation.Nonnull;
import nl.javadude.gradle.plugins.license.LicenseExtension;
import nl.javadude.gradle.plugins.license.LicensePlugin;
import org.ajoberstar.gradle.git.ghpages.GithubPagesPlugin;
import org.ajoberstar.gradle.git.ghpages.GithubPagesPluginExtension;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.FileTree;
import org.gradle.api.plugins.ExtraPropertiesExtension;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.plugins.ProjectReportsPlugin;
import org.gradle.api.plugins.announce.BuildAnnouncementsPlugin;
import org.gradle.api.reporting.plugins.BuildDashboardPlugin;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.SourceTask;
import org.gradle.api.tasks.javadoc.Groovydoc;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.api.tasks.scala.ScalaDoc;
import org.gradle.api.tasks.wrapper.Wrapper;

/**
 * The standard project plugin.
 *
 * @author shevek
 */
public class StdProjectPlugin implements Plugin<Project> {

    public static <T> T getExtraPropertyOrNull(@Nonnull Project project, @Nonnull String name) {
        ExtraPropertiesExtension properties = project.getExtensions().getExtraProperties();
        if (properties.has(name))
            return (T) properties.get(name);
        return null;
    }

    @Nonnull
    public static String getGithubUser(@Nonnull Project project) {
        String githubUserName = getExtraPropertyOrNull(project, "githubUserName");
        if (githubUserName == null)
            githubUserName = System.getProperty("user.name");
        return githubUserName;
    }

    /**
     * Returns a string of the form githubUserName/githubProjectName.
     *
     * Uses project.githubUserName and project.githubProjectName properties.
     * Falls back to project.getName() and System.getProperty("user.name").
     *
     * @param project The project to inspect. The root project is used.
     * @return a string of the form githubUserName/githubProjectName.
     */
    @Nonnull
    public static String getGithubPath(@Nonnull Project project) {
        project = project.getRootProject();

        String githubProjectName = getExtraPropertyOrNull(project, "githubProjectName");
        if (githubProjectName == null)
            githubProjectName = project.getName();

        String githubUserName = getGithubUser(project);

        return githubUserName + "/" + githubProjectName;
    }

    @Override
    public void apply(final Project project) {
        final StdProjectExtension extension = project.getExtensions().create("stdproject", StdProjectExtension.class, project);

        // Convention
        project.getPlugins().apply(BuildAnnouncementsPlugin.class);
        project.getPlugins().apply(BuildDashboardPlugin.class);
        project.getPlugins().apply(ProjectReportsPlugin.class);

        Wrapper wrapper = (Wrapper) project.getTasks().getByName("wrapper");
        wrapper.setGradleVersion("2.2.1");

        // Github
        project.getPlugins().apply(GithubPagesPlugin.class);

        final GithubPagesPluginExtension githubPages = project.getExtensions().getByType(GithubPagesPluginExtension.class);
        githubPages.setRepoUri("git@github.com:" + getGithubPath(project) + ".git");

        // Github - aggregate documentation
        for (final Class<? extends SourceTask> docTaskClass : Arrays.asList(Javadoc.class, ScalaDoc.class, Groovydoc.class)) {

            project.getLogger().info("Aggregating " + docTaskClass.getSimpleName() + " for " + project);

            /* We're still in stdproject - no modules have been configured yet.
             EXISTS:
             {
             // Ensure at least one task of the type exists.
             for (Project subproject : project.getAllprojects())
             for (SourceTask docTask : subproject.getTasks().withType(docTaskClass))
             break EXISTS;
             continue;
             }
             */
            final String shortName = docTaskClass.getSimpleName().toLowerCase();
            project.getTasks().create("aggregate" + docTaskClass.getSimpleName(), docTaskClass, new Action<SourceTask>() {
                @Override
                public void execute(final SourceTask t) {
                    t.setProperty("destinationDir", new File(project.getBuildDir(), "docs/" + shortName));
                    t.doFirst(new Action<Task>() {
                        @Override
                        public void execute(Task _t) {
                            // TODO: t.source(...);
                            // TODO: t.setProperty("classpath", null);
                        }
                    });
                    if (t instanceof Javadoc)
                        StdModulePlugin.configureJavadoc(project, (Javadoc) t);
                    // githubPages.getPages().from(t.getOutputs().getFiles()).into("docs/" + shortName);
                    githubPages.getPages().from(t).into("docs/" + shortName);

                    t.conventionMapping("source", new Callable<FileTree>() {
                        @Override
                        public FileTree call() throws Exception {
                            FileCollection sources = project.files();
                            for (Project subproject : project.getAllprojects()) {
                                t.getLogger().info("Searching " + subproject + " for " + docTaskClass.getSimpleName());
                                for (SourceTask docTask : subproject.getTasks().withType(docTaskClass)) {
                                    if (docTask == t)
                                        continue;   // We exist now; skip ourselves or we recurse into this convention.
                                    t.getLogger().info("Adding sources from " + docTask + ": " + docTask.getSource());
                                    sources = sources.plus(docTask.getSource());
                                }
                            }

                            t.getLogger().info("Sources are " + sources);

                            return sources.getAsFileTree();
                        }
                    });

                    t.conventionMapping("classpath", new Callable<FileCollection>() {
                        @Override
                        public FileCollection call() throws Exception {
                            FileCollection classpath = project.files();
                            for (Project subproject : project.getAllprojects()) {
                                if (!subproject.getPlugins().hasPlugin(JavaPlugin.class))
                                    continue;
                                SourceSetContainer sourceSets = subproject.getConvention().getPlugin(JavaPluginConvention.class).getSourceSets();
                                classpath = classpath.plus(sourceSets.getByName("main").getCompileClasspath());
                            }
                            return classpath;
                        }
                    });

                }
            });

        }

        // License
        for (Project subproject : project.getAllprojects()) {
            subproject.getPlugins().apply(LicensePlugin.class);
            LicenseExtension license = subproject.getExtensions().getByType(LicenseExtension.class);
            license.setHeader(project.file("codequality/HEADER"));
            // license.ext.year = Calendar.getInstance().get(Calendar.YEAR);
            license.setSkipExistingHeaders(true);
            license.setIgnoreFailures(true);
        }
        // githubPages.getPages().from(t).into("docs/" + shortName);

        // Misc
        project.getTasks().create("buildDependencies", BuildDependencies.class);
    }

}
