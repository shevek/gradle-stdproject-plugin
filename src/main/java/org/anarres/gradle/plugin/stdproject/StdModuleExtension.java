package org.anarres.gradle.plugin.stdproject;

import com.google.common.base.Objects;
import groovy.lang.Closure;
import groovy.lang.GroovyObjectSupport;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import org.gradle.api.Project;
import org.gradle.util.ConfigureUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The standard module plugin extension.
 *
 * This allows configuring the standard module plugin using a
 * <code>stdmodule { }</code> block.
 *
 * @author shevek
 */
public class StdModuleExtension extends GroovyObjectSupport {

    private static final Logger LOG = LoggerFactory.getLogger(StdModuleExtension.class);
    private static final String GITHUB_URL_PREFIX = "https://github.com/";

    public static class Person extends GroovyObjectSupport {

        public String id;
        public String name;
        public String email;

        public void id(@Nonnull String id) {
            this.id = id;
        }

        public void name(@Nonnull String name) {
            this.name = name;
        }

        public void email(@Nonnull String email) {
            this.email = email;
        }

        @Override
        public String toString() {
            return Objects.toStringHelper(this)
                    .add("id", id)
                    .add("name", name)
                    .add("email", email)
                    .toString();
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
        projectIssuesUrl = GITHUB_URL_PREFIX + githubPath + "/issues";
        projectVcsUrl = "scm:git:git@github.com:" + githubPath + ".git";
    }

    public void projectDescription(@Nonnull String projectDescription) {
        this.projectDescription = projectDescription;
    }

    public void projectAuthor(@Nonnull Closure c) {
        Person author = new Person();
        ConfigureUtil.configure(c, author);
        LOG.info("Adding author " + author);
        projectAuthors.add(author);
    }
}
