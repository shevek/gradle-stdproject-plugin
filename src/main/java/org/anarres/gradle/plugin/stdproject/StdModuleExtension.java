package org.anarres.gradle.plugin.stdproject;

import groovy.lang.Closure;
import groovy.lang.GroovyObjectSupport;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import org.gradle.api.Project;
import org.gradle.util.ConfigureUtil;

/**
 * The standard module plugin extension.
 *
 * This allows configuring the standard module plugin using a
 * <code>stdmodule { }</code> block.
 *
 * @author shevek
 */
public class StdModuleExtension extends GroovyObjectSupport {

    private static final String GITHUB_URL_PREFIX = "https://github.com/";

    public static class Person extends GroovyObjectSupport {

        public String id;
        public String name;
        public String email;

        public void id(String id) {
            this.id = id;
        }

        public void name(String name) {
            this.name = name;
        }

        public void email(String email) {
            this.email = email;
        }
    }

    public static class License extends GroovyObjectSupport {
    }

    public String projectDescription;
    public final String projectUrl;
    public final String projectIssuesUrl;
    public final String projectVcsUrl;
    public String projectInceptionYear;
    public List<Person> projectAuthors = new ArrayList<Person>();

    public StdModuleExtension(@Nonnull Project project) {
        String githubPath = StdProjectPlugin.getGithubPath(project);
        projectUrl = GITHUB_URL_PREFIX + githubPath;
        projectIssuesUrl = GITHUB_URL_PREFIX + githubPath + ".issues";
        projectVcsUrl = "scm:git:git@github.com:" + githubPath + ".git";
    }

    public void projectDescription(@Nonnull String projectDescription) {
        this.projectDescription = projectDescription;
    }

    public void projectAuthor(@Nonnull Closure c) {
        Person author = new Person();
        ConfigureUtil.configure(c, author);
        projectAuthors.add(author);
    }
}
