/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.gradle.plugin.stdproject;

import com.jfrog.bintray.gradle.BintrayExtension;
import com.jfrog.bintray.gradle.BintrayPlugin;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 *
 * @author shevek
 */
public class StdPluginPlugin implements Plugin<Project> {

    @Override
    public void apply(Project t) {
        t.getPlugins().apply(BintrayPlugin.class);
        BintrayExtension bintray = t.getExtensions().getByType(BintrayExtension.class);
        String bintrayUsername = StdProjectPlugin.getExtraPropertyOrNull(t.getRootProject(), "bintrayUsername");
        if (bintrayUsername != null)
            bintray.setUser(bintrayUsername);
        String bintrayApiKey = StdProjectPlugin.getExtraPropertyOrNull(t.getRootProject(), "bintrayApiKey");
        if (bintrayApiKey != null)
            bintray.setKey(bintrayApiKey);
        bintray.getPkg().setRepo("gradle-plugins");
    }

}
