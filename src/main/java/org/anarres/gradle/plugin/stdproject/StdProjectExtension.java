package org.anarres.gradle.plugin.stdproject;

import groovy.lang.GroovyObjectSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import org.gradle.api.Project;

/**
 * The standard project plugin extension.
 *
 * This allows configuring the standard project plugin using a
 * <code>stdproject { }</code> block.
 *
 * @author shevek
 */
public class StdProjectExtension extends GroovyObjectSupport {

    public StdProjectExtension(@Nonnull Project project) {
    }

    public Map<String, List<String>> javadocGroups = new LinkedHashMap<String, List<String>>();

    public void javadocGroup(@Nonnull String title, @Nonnull String... patterns) {
        List<String> group = javadocGroups.get(title);
        if (group == null) {
            group = new ArrayList<String>();
            javadocGroups.put(title, group);
        }
        group.addAll(Arrays.asList(patterns));
    }

    // aggregateJavadoc requires this.
    public List<String> javadocLinks = new ArrayList<String>() {
        {
            addAll(Arrays.asList(
                    "http://docs.oracle.com/javase/7/docs/api/",
                    "http://docs.oracle.com/javaee/7/api/",
                    "http://gradle.org/docs/current/javadoc/",
                    // "http://groovy.codehaus.org/api/", // obsolete
                    // "http://www.groovy-lang.org/api/",
                    "http://docs.guava-libraries.googlecode.com/git/javadoc/"));
        }
    };

    public void javadocLink(@Nonnull Object... uris) {
        for (Object uri : uris)
            javadocLinks.add(String.valueOf(uri));
    }
    public boolean javadocLinkSource = true;

    public void javadocLinkSource(boolean value) {
        this.javadocLinkSource = value;
    }

    public boolean javadocQuiet = false;

    public void javadocQuiet(boolean value) {
        this.javadocQuiet = value;
    }
}
