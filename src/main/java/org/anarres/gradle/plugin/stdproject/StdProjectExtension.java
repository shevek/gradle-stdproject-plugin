package org.anarres.gradle.plugin.stdproject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The standard project plugin extension.
 *
 * This allows configuring the standard project plugin using a
 * <code>stdproject { }</code> block.
 *
 * @author shevek
 */
public class StdProjectExtension {

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

}
