package org.anarres.gradle.plugin.stdproject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The standard module plugin extension.
 *
 * This allows configuring the standard module plugin using a
 * <code>stdmodule { }</code> block.
 *
 * @author shevek
 */
public class StdModuleExtension {

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
