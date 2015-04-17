package org.anarres.gradle.plugin.stdproject;

import groovy.lang.GroovyObjectSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    // aggregateJavadoc requires this.
    public List<String> javadocLinks = new ArrayList<String>() {
        {
            addAll(Arrays.asList(
                    "http://docs.oracle.com/javase/7/docs/api/",
                    "http://docs.oracle.com/javaee/7/api/",
                    "http://gradle.org/docs/current/javadoc/",
                    "http://groovy.codehaus.org/api/",
                    "http://docs.guava-libraries.googlecode.com/git/javadoc/"));
        }
    };
    public boolean javadocQuiet = false;

    public void javadocLink(@Nonnull String uri) {
        javadocLinks.add(uri);
    }

    public void javadocQuiet(boolean value) {
        this.javadocQuiet = value;
    }
}
