package org.anarres.gradle.plugin.stdproject;

import java.util.Collections;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author shevek
 */
public class StdProjectPluginApplyTest {

    Project project;

    @Before
    public void setUp() {
        project = ProjectBuilder.builder().build();
    }

    @Test
    public void testApply() {
        // project.apply(Collections.singletonMap("plugin", "java"));
        // project.apply(Collections.singletonMap("plugin", "stdproject"));
        // assertTrue("Project is missing plugin", project.getPlugins().hasPlugin(StdProjectPlugin.class));
    }
}
