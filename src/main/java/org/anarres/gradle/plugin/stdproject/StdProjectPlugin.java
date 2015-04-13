package org.anarres.gradle.plugin.stdproject;

import java.util.Arrays;
import org.ajoberstar.gradle.git.ghpages.GithubPagesPlugin;
import org.ajoberstar.gradle.git.ghpages.GithubPagesPluginExtension;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.DefaultGroovyStaticMethods;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.FileCollection;
import org.gradle.api.plugins.ProjectReportsPlugin;
import org.gradle.api.plugins.announce.BuildAnnouncementsPlugin;
import org.gradle.api.reporting.plugins.BuildDashboardPlugin;
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
        for (Class<? extends SourceTask> docTaskClass : Arrays.asList(Javadoc.class, ScalaDoc.class, Groovydoc.class)) {

            FileCollection sources = project.files();
            for (Project subproject : project.getAllprojects())
                for (SourceTask docTask : subproject.getTasks().withType(docTaskClass))
                    sources = sources.plus(docTask.getSource());

            if (sources.isEmpty())
                continue;

            project.getTasks().create("aggregate" + docTaskClass.getSimpleName(), docTaskClass, new Action<SourceTask>() {
                @Override
                public void execute(SourceTask t) {
                    // t.source(sources);
                    // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }
            });

            String shortName = docTaskClass.getSimpleName().toLowerCase();

        }

        GithubPagesPluginExtension githubPages = project.getExtensions().getByType(GithubPagesPluginExtension.class);
        // githubPages.setRepoUri(this);
        // githubPages.getPages().from(t.getOutputs().getFiles()).into("docs/${shortName}");

    }

}
