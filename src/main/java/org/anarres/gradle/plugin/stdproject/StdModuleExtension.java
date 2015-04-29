package org.anarres.gradle.plugin.stdproject;

import com.google.common.base.Objects;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.lang.GroovyObjectSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
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

    public String description;
    public final String url;
    public final String issuesSystem = "github";
    public final String issuesUrl;
    public final String vcsUrl;
    public String inceptionYear;
    public List<Person> authors = new ArrayList<Person>();
    public List<License> licenses = new ArrayList<License>();

    public StdModuleExtension(@Nonnull Project project) {
        String githubPath = StdProjectPlugin.getGithubPath(project);
        url = GITHUB_URL_PREFIX + githubPath;
        issuesUrl = GITHUB_URL_PREFIX + githubPath + "/issues";
        vcsUrl = "scm:git:git@github.com:" + githubPath + ".git";
    }

    public void description(@Nonnull String description) {
        this.description = description;
    }

    public void author(@Nonnull Map<String, Object> m) {
        Person person = new Person();
        ConfigureUtil.configureByMap(m, person);
        authors.add(person);
    }

    public void author(@Nonnull @DelegatesTo(Person.class) Closure c) {
        Person person = new Person();
        ConfigureUtil.configure(c, person);
        // LOG.info("Adding author " + person);
        authors.add(person);
    }

    public void license(@Nonnull String name) {
        License license = License.LICENSES.get(name);
        if (license == null)
            throw new IllegalArgumentException("Unknown license " + name + "; available are " + License.LICENSES.keySet() + " or configure with a Closure.");
        licenses.add(license);
    }

    public void license(@Nonnull Map<String, Object> m) {
        License license = new License();
        ConfigureUtil.configureByMap(m, license, DefaultGroovyMethods.getProperties(license).keySet());
        licenses.add(license);
    }

    public void license(@Nonnull @DelegatesTo(License.class) Closure closure) {
        License license = new License();
        ConfigureUtil.configure(closure, license);
        licenses.add(license);
    }
}
