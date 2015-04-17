package org.anarres.gradle.plugin.stdproject;

import groovy.lang.GroovyObjectSupport;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import org.gradle.api.Project;

/**
 * The standard plugin plugin extension.
 *
 * This allows configuring the standard plugin plugin using a
 * <code>stdplugin { }</code> block.
 *
 * @author shevek
 */
public class StdPluginExtension extends GroovyObjectSupport {

    public String pluginName;
    public String pluginDescription;
    public Map<String, String> pluginImplementations = new HashMap<String, String>();
    public final Set<String> pluginLicenses = new LinkedHashSet<String>();
    public final Set<String> pluginTags = new LinkedHashSet<String>();

    public StdPluginExtension(@Nonnull Project project) {
        pluginName = project.getName();
        pluginLicenses.add("Apache-2.0");
    }

    public void pluginImplementation(@Nonnull String id, @Nonnull String implementationClass) {
        pluginImplementations.put(id, implementationClass);
    }

    public void pluginImplementation(@Nonnull String implementationClass) {
        String id = implementationClass.
                replaceAll("\\.gradle\\.", ".").
                replaceAll("\\.plugin\\.", ".").
                substring(0, implementationClass.lastIndexOf('.'));
        pluginImplementation(id, implementationClass);
    }

}
