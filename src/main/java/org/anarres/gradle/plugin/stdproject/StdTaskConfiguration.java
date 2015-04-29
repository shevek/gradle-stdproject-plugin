/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.gradle.plugin.stdproject;

import javax.annotation.Nonnull;
import org.gradle.api.Action;
import org.gradle.api.JavaVersion;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.external.javadoc.StandardJavadocDocletOptions;

/**
 *
 * @author shevek
 */
public class StdTaskConfiguration {

    public static void configureJavadoc(@Nonnull final Project project, @Nonnull final Javadoc javadoc) {
        final StdProjectExtension extension = project.getRootProject().getExtensions().getByType(StdProjectExtension.class);
        final StandardJavadocDocletOptions javadocOptions = (StandardJavadocDocletOptions) javadoc.getOptions();
        javadoc.doFirst(new Action<Task>() {
            @Override
            public void execute(Task t) {
                if (!t.getProject().getGradle().getStartParameter().isOffline())
                    javadocOptions.setLinks(extension.javadocLinks);
                javadocOptions.setLinkSource(extension.javadocLinkSource);
                if (!extension.javadocGroups.isEmpty())
                    javadocOptions.setGroups(extension.javadocGroups);
                if (JavaVersion.current().isJava8Compatible())
                    if (extension.javadocQuiet)
                        javadocOptions.addStringOption("Xdoclint:none", "-quiet");
            }
        });
    }
}
