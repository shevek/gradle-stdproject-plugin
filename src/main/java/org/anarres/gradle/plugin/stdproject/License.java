/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.gradle.plugin.stdproject;

import groovy.lang.GroovyObjectSupport;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;

/**
 *
 * @author shevek
 */
public class License extends GroovyObjectSupport {

    public static final Map<String, License> LICENSES = new HashMap<String, License>() {
        private void add(@Nonnull String name, @Nonnull String title, @Nonnull String url) {
            put(name, new License(name, title, URI.create(url)));
        }

        {
            add("Apache-2.0", "Apache License, Version 2.0", "http://www.apache.org/licenses/LICENSE-2.0.html");
            add("GPL-3.0", "GNU General Public License, Version 3.0", "http://www.gnu.org/licenses/gpl.html");
            add("GPL-2.0", "GNU General Public License, Version 2.0", "http://www.gnu.org/licenses/old-licenses/gpl-2.0.html");
            add("GPL-1.0", "GNU General Public License, Version 1.0", "http://www.gnu.org/licenses/old-licenses/gpl-1.0.html");
            add("LGPL-3.0", "GNU Lesser General Public License, Version 3.0", "http://www.gnu.org/licenses/lgpl.html");
            add("LGPL-2.1", "GNU Lesser General Public License, Version 2.1", "http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html");
            add("MIT", "MIT License", "http://opensource.org/licenses/MIT");
        }
    };
    private String name;
    private String description;
    private URI uri;

    public License() {
    }

    public License(@Nonnull String name, @Nonnull String description, @Nonnull URI uri) {
        this.name = name;
        this.description = description;
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(@Nonnull String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(@Nonnull String description) {
        this.description = description;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(@Nonnull URI uri) {
        this.uri = uri;
    }

    public void setUri(@Nonnull String uri) {
        setUri(URI.create(uri));
    }

}
