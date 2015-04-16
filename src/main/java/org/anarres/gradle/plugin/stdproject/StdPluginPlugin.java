/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.gradle.plugin.stdproject;

import com.jfrog.bintray.gradle.BintrayExtension;
import com.jfrog.bintray.gradle.BintrayPlugin;
import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Callable;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSetContainer;

/**
 *
 * @author shevek
 */
public class StdPluginPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        final StdPluginExtension extension = project.getExtensions().create("stdplugin", StdPluginExtension.class);

        // Bintray
        project.getPlugins().apply(BintrayPlugin.class);
        BintrayExtension bintray = project.getExtensions().getByType(BintrayExtension.class);
        String bintrayUsername = StdProjectPlugin.getExtraPropertyOrNull(project.getRootProject(), "bintrayUsername");
        if (bintrayUsername != null)
            bintray.setUser(bintrayUsername);
        String bintrayApiKey = StdProjectPlugin.getExtraPropertyOrNull(project.getRootProject(), "bintrayApiKey");
        if (bintrayApiKey != null)
            bintray.setKey(bintrayApiKey);
        bintray.getPkg().setRepo("gradle-plugins");

        // Plugin
        final File pluginDescriptorDir = new File(project.getBuildDir(), "generated-resources/gradle-plugin");
        Task generatePluginDescriptors = project.getTasks().create("generatePluginDescriptors", GeneratePluginDescriptors.class, new Action<GeneratePluginDescriptors>() {
            @Override
            public void execute(GeneratePluginDescriptors t) {
                t.setDestinationDir(pluginDescriptorDir);
                t.conventionMapping("pluginImplementations", new Callable<Map<String, String>>() {
                    @Override
                    public Map<String, String> call() throws Exception {
                        return extension.pluginImplementations;
                    }
                });
            }
        });
        SourceSetContainer sourceSets = project.getConvention().getPlugin(JavaPluginConvention.class).getSourceSets();
        sourceSets.getByName("main").getOutput().dir(Collections.<String, Object>singletonMap("builtBy", generatePluginDescriptors), pluginDescriptorDir);
    }

}
