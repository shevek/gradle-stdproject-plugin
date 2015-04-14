package org.anarres.gradle.plugin.stdproject;

import java.io.File;
import java.util.Arrays;
import javax.annotation.Nonnull;
import org.ajoberstar.gradle.git.ghpages.GithubPagesPlugin;
import org.ajoberstar.gradle.git.ghpages.GithubPagesPluginExtension;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.file.FileCollection;
import org.gradle.api.plugins.ExtraPropertiesExtension;
import org.gradle.api.plugins.ProjectReportsPlugin;
import org.gradle.api.plugins.announce.BuildAnnouncementsPlugin;
import org.gradle.api.reporting.plugins.BuildDashboardPlugin;
import org.gradle.api.tasks.SourceTask;
import org.gradle.api.tasks.javadoc.Groovydoc;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.api.tasks.scala.ScalaDoc;
import org.gradle.api.tasks.wrapper.Wrapper;
import org.gradle.external.javadoc.StandardJavadocDocletOptions;

/**
 * The standard project plugin.
 *
 * @author shevek
 */
public class StdProjectPlugin implements Plugin<Project> {

    private static interface DocTaskHandler<T extends SourceTask> {

        @Nonnull
        public Class<T> getDocTaskClass();

        public void setClassPath(@Nonnull T task, @Nonnull FileCollection classpath);

        public void setDestinationDir(@Nonnull T task, @Nonnull File dir);
    }

    private static class JavadocTaskHandler implements DocTaskHandler<Javadoc> {

        @Override
        public Class<Javadoc> getDocTaskClass() {
            return Javadoc.class;
        }

        @Override
        public void setClassPath(Javadoc task, FileCollection classpath) {
            task.setClasspath(classpath);
        }

        @Override
        public void setDestinationDir(Javadoc task, File dir) {
            task.setDestinationDir(dir);
        }
    }

    private static class ScaladocTaskHandler implements DocTaskHandler<ScalaDoc> {

        @Override
        public Class<ScalaDoc> getDocTaskClass() {
            return ScalaDoc.class;
        }

        @Override
        public void setClassPath(ScalaDoc task, FileCollection classpath) {
            task.setClasspath(classpath);
        }

        @Override
        public void setDestinationDir(ScalaDoc task, File dir) {
            task.setDestinationDir(dir);
        }
    }

    private static class GroovydocTaskHandler implements DocTaskHandler<Groovydoc> {

        @Override
        public Class<Groovydoc> getDocTaskClass() {
            return Groovydoc.class;
        }

        @Override
        public void setClassPath(Groovydoc task, FileCollection classpath) {
            task.setClasspath(classpath);
        }

        @Override
        public void setDestinationDir(Groovydoc task, File dir) {
            task.setDestinationDir(dir);
        }
    }

    private static Object getExtraPropertyOrNull(@Nonnull Project project, @Nonnull String name) {
        ExtraPropertiesExtension properties = project.getExtensions().getExtraProperties();
        if (properties.has(name))
            return properties.get(name);
        return null;
    }

    @Override
    public void apply(final Project project) {
        final StdProjectExtension extension = project.getExtensions().create("stdproject", StdProjectExtension.class);

        // Convention
        project.getPlugins().apply(BuildAnnouncementsPlugin.class);
        project.getPlugins().apply(BuildDashboardPlugin.class);
        project.getPlugins().apply(ProjectReportsPlugin.class);

        Wrapper wrapper = (Wrapper) project.getTasks().getByName("wrapper");
        wrapper.setGradleVersion("2.2.1");

        // Github
        project.getPlugins().apply(GithubPagesPlugin.class);

        String githubProjectName = (String) getExtraPropertyOrNull(project, "githubProjectName");
        if (githubProjectName == null)
            githubProjectName = project.getName();

        String githubUserName = (String) getExtraPropertyOrNull(project, "githubUserName");
        if (githubUserName == null)
            githubUserName = System.getProperty("user.name");

        final GithubPagesPluginExtension githubPages = project.getExtensions().getByType(GithubPagesPluginExtension.class);
        githubPages.setRepoUri("git@github.com:" + githubUserName + "/" + githubProjectName + ".git");

        // Github - aggregate documentation
        project.getPlugins().apply(GithubPagesPlugin.class);
        for (Class<? extends SourceTask> docTaskClass : Arrays.asList(Javadoc.class, ScalaDoc.class, Groovydoc.class)) {

            project.getLogger().info("Aggregating " + docTaskClass.getSimpleName() + " for " + project);

            FileCollection sources = project.files();
            for (Project subproject : project.getAllprojects()) {
                project.getLogger().info("Searching " + subproject + " for " + docTaskClass.getSimpleName());
                for (SourceTask docTask : subproject.getTasks().withType(docTaskClass)) {
                    project.getLogger().info("Adding sources from " + docTask + ": " + docTask.getSource());
                    sources = sources.plus(docTask.getSource());
                }
            }

            project.getLogger().info("Sources are " + sources);

            if (sources.isEmpty())
                continue;

            final String shortName = docTaskClass.getSimpleName().toLowerCase();
            final FileCollection _sources = sources;
            project.getTasks().create("aggregate" + docTaskClass.getSimpleName(), docTaskClass, new Action<SourceTask>() {
                @Override
                public void execute(final SourceTask t) {
                    t.source(_sources);
                    t.setProperty("destinationDir", new File(project.getBuildDir(), "docs/" + shortName));
                    t.doFirst(new Action<Task>() {
                        @Override
                        public void execute(Task t) {
                            // TODO: t.setProperty("classpath", null);
                        }
                    });
                    if (t instanceof Javadoc)
                        StdModulePlugin.configureJavadoc(project, (Javadoc) t);
                    // githubPages.getPages().from(t.getOutputs().getFiles()).into("docs/" + shortName);
                    githubPages.getPages().from(t).into("docs/" + shortName);
                }
            });

        }

    }

}
