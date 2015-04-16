package org.anarres.gradle.plugin.stdproject;

import java.util.HashMap;
import java.util.Map;

/**
 * The standard plugin plugin extension.
 *
 * This allows configuring the standard plugin plugin using a
 * <code>stdplugin { }</code> block.
 *
 * @author shevek
 */
public class StdPluginExtension {

    public Map<String, String> pluginImplementations = new HashMap<String, String>();

}
