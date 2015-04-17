/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.gradle.plugin.stdproject;

import java.io.File;
import java.util.SortedSet;
import java.util.TreeSet;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.initialization.dsl.ScriptHandler;
import org.gradle.api.internal.ConventionTask;
import org.gradle.api.tasks.TaskAction;

/**
 *
 * @author shevek
 */
public class BuildDependencies extends ConventionTask {

    @TaskAction
    public void run() {
        SortedSet<String> dependencies = new TreeSet<String>();
        ConfigurationContainer container = getProject().getBuildscript().getConfigurations();
        for (File file : container.getByName(ScriptHandler.CLASSPATH_CONFIGURATION))
            dependencies.add(file.getName());
        for (String dependency : dependencies)
            System.out.println(dependency);

    }
}
