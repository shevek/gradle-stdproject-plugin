/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.gradle.plugin.stdproject;

import java.io.File;
import java.util.Map;
import javax.annotation.Nonnull;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.gradle.api.internal.ConventionTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

/**
 *
 * @author shevek
 */
public class GeneratePluginDescriptors extends ConventionTask {

    private File destinationDir;
    private Map<String, String> pluginImplementations;

    @OutputDirectory
    public File getDestinationDir() {
        return destinationDir;
    }

    public void setDestinationDir(@Nonnull File destinationDir) {
        this.destinationDir = destinationDir;
    }

    @Input
    public Map<String, String> getPluginImplementations() {
        return pluginImplementations;
    }

    public void setPluginImplementations(@Nonnull Map<String, String> pluginImplementations) {
        this.pluginImplementations = pluginImplementations;
    }

    @TaskAction
    public void generate() throws Exception {
        final File destinationDir = getDestinationDir();
        DefaultGroovyMethods.deleteDir(destinationDir);
        for (Map.Entry<String, ? extends Object> e : getPluginImplementations().entrySet()) {
            File pluginDescriptorFile = new File(destinationDir, "META-INF/gradle-plugins/" + e.getKey() + ".properties");
            pluginDescriptorFile.getParentFile().mkdirs();
            DefaultGroovyMethods.setText(pluginDescriptorFile,
                    "implementation-class=" + e.getValue() + "\n"
                    + "implementation-version=" + getProject().getVersion() + "\n");
        }
    }
}
