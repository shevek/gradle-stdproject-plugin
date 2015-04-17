/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.gradle.plugin.stdproject;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;

/**
 *
 * @author shevek
 */
public class StandardLicense {

    public static final Map<String, StandardLicense> LICENSES = new HashMap<String, StandardLicense>() {
        private void add(String name, String title, String url) {
            put(name, new StandardLicense(name, title, URI.create(url)));
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
    private final String name;
    private final String description;
    private final URI uri;

    public StandardLicense(@Nonnull String name, @Nonnull String description, @Nonnull URI uri) {
        this.name = name;
        this.description = description;
        this.uri = uri;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Nonnull
    public String getDescription() {
        return description;
    }

    @Nonnull
    public URI getUri() {
        return uri;
    }

}
