/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tinkerpop.gremlin.groovy.util;

import groovy.lang.GroovyClassLoader;
import java.io.File;
import org.apache.commons.io.FileUtils;
import org.apache.tinkerpop.gremlin.groovy.plugin.Artifact;
import org.apache.tinkerpop.gremlin.util.Gremlin;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Jason Plurad (http://github.com/pluradj)
 */
public class DependencyGrabberTest {
    private static final GroovyClassLoader dummyClassLoader = new GroovyClassLoader();
    private static final File extTestDir = new File(System.getProperty("user.dir"), "/target/test-dep-ext");
    DependencyGrabber dg = null;

    @Before
    public void setUp() {
        FileUtils.deleteQuietly(extTestDir);
        dg = new DependencyGrabber(dummyClassLoader, extTestDir.getAbsolutePath());
    }

    @After
    public void tearDown() {
        FileUtils.deleteQuietly(extTestDir);
    }

    @Test
    public void shouldInstallAndUninstallDependencies() {
        final String pkg = "org.apache.tinkerpop";
        final String name = "gremlin-groovy";
        final String ver = Gremlin.version();
        final Artifact a = new Artifact(pkg, name, ver);

        // install the plugin
        final File pluginDir = new File(extTestDir, name);
        dg.copyDependenciesToPath(a);
        assertTrue(pluginDir.exists());

        // delete the plugin
        dg.deleteDependenciesFromPath(a);
        assertFalse(pluginDir.exists());
    }

    @Test(expected=IllegalStateException.class)
    public void shouldThrowIllegalStateException() {
        final String pkg = "org.apache.tinkerpop";
        final String name = "gremlin-groovy";
        final String ver = Gremlin.version();
        final Artifact a = new Artifact(pkg, name, ver);

        // install the plugin for the first time
        dg.copyDependenciesToPath(a);
        final File pluginDir = new File(extTestDir, name);
        assertTrue(pluginDir.exists());

        // attempt to install plugin a second time
        try {
            dg.copyDependenciesToPath(a);
        } catch (IllegalStateException ise) {
            // validate that the plugin dir wasn't deleted by accident
            assertTrue(pluginDir.exists());
            // throw the IllegalStateException
            throw ise;
        }
    }
}
